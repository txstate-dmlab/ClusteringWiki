/**
 *  ClusteringWiki - personalized and collaborative clustering of search results
 *  Copyright (C) 2010  Texas State University-San Marcos
 *  
 *  Contact: http://dmlab.cs.txstate.edu
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
 /**
* Editable Cluster tree, part of the ClusteringWiki research project
* http://dmlab.cs.txstate.edu/ClusteringWiki/
* 
* While the editableClusterTree was written from scratch for this application,
* some inspiration came from:
* - jQuery SimpleTree Drag&Drop plugin Version 0.3 (http://plugins.jquery.com/project/SimpleTree)
* - treeOperations by  Ahmet Oguz Mermerkaya (ahmetmermerkaya@hotmail.com) (http://code.google.com/p/editable-jquery-tree-with-php-codes/)
* - Style sheets were borrowed heavily from treeOperations.
* - Images were customized from the FamFamFam Silk icon set - http://famfamfam.com/lab/icons/silk/
* 
* Please note that the following element ids are reserved by the editable cluster tree:
* ecTreeMenu, ectMenuEditDisabled, ecTreeUl, ecTreeRoot, ecTreeInput
* Certain classes defined in ecTree.css should also not be reused as they may alter the look
* or functionality of the editable cluster tree.
* 
* Author: David C. Anastasiu
* @since 2010.09.06
*/


function editableClusterTree(){
	
	var Tree = this;                            //get object reference
	//options
	this.treeDivElementId = 'clusterTree';      //where to place representation of the tree
	this.treeMessageElementId = 'ectMessage';   //where to show tree related messages
	this.resultsDivElementId = 'response';      //where to show results
	this.resultsFooterDivElementId = 'responseFooter';  //where to show results page info and links
	this.statsDivElementId = 'stats';           //where to show stats
	this.editLogElementId = 'clusterEditsMsg';  //a div to show cluster edits being sent to server
	this.timersElementId = 'clusterTimesMsg';   //a div to show algo execution times
	this.animationSpeed = 'fast';               //how fast should the tree changes be animated
	this.debug = false;                         //whether to show additional debug data
	this.debugDivElementId = false;             //where to place debug info
	this.searchFunctionName = 'processQ';       //what function to call to execute search
	this.highlightFunction = false;             //a function accepting 'label' and 'text' parameters 
	                                            //and returning 'text' with 'label' words highlighted
	this.maximumLabelLength = 100;				//how long a label can be
	this.editAllowed = false;                   //whether editing the tree is enabled
	this.dragAllowed = false;                   //whether drag and drop is enabled
	this.taggingEnabled = false;                //whether tagging is enabled
	this.taggingElementId = 'remainingTagCount'; //element to use for reminaing tag count message
	this.disabledMessage = 'Please log in to enable editing.'; //message when editing disabled
	
	//options that can be modified
	this.options = {                            //options that can be overwritten by constructor
		treeDivElementId: true,                 //options object parameter
		treeMessageElementId: true,
		resultsDivElementId: true,
		resultsFooterDivElementId: true,
		statsDivElementId: true,
		editLogElementId: true,
		timersElementId: true,
		animationSpeed: true,
		debug: true,
		debugDivElementId: true,
		searchFunctionName: true,
		highlightFunction: true,
		maximumLabelLength: true,
		editAllowed: true,
		dragAllowed: true,
		taggingEnabled : true,
		disabledMessage: true
	};
	
	/**
	 * Allow passing a parameters object
	 */
	for (n in arguments[0]) 
	{ 
		if(n in this.options && this.options[n])
			this[n] = arguments[0][n]; 
	}
	
	//internal data
	this.contextMenuDiv = '<div class="contextMenu" id="ecTreeMenu">' +
	'	<span class="addNode"><img src="includes/images/tree/node_add.png" /> Create internal label </span>' +
	'	<span class="addBottomNode"><img src="includes/images/tree/bottom_node_add.png" /> Create bottom label </span>' +
	'	<span class="copyNode"><img src="includes/images/tree/node_copy.png" /> Copy label </span>' +
	'	<span class="copyResult"><img src="includes/images/tree/page_copy.png" /> Copy result </span>' +
	'	<span class="cutNode"><img src="includes/images/tree/node_cut.png" /> Cut label </span>' +
	'	<span class="cutResult"><img src="includes/images/tree/page_cut.png" /> Cut result </span>' +
	'	<span class="pasteNode"><img src="includes/images/tree/node_paste.png" /> Paste label </span>' +
	'	<span class="pasteResult"><img src="includes/images/tree/page_paste.png" /> Paste result </span>' +
	'	<span class="rename"><img src="includes/images/tree/node_rename.png" /> Rename label </span>' +
	'	<span class="deleteNode"><img src="includes/images/tree/node_delete.png" /> Delete label </span>' +
	'	<span class="deleteResult"><img src="includes/images/tree/page_delete.png" /> Delete result </span>' +
	'	<span class="clearAllEdits"><img src="includes/images/tree/tree_clear_all_edits.png"/> Clear all edits </span>' +
	'	<span class="showWithoutEdits"><img src="includes/images/tree/tree_without_edits.png"/> Show tree w/o edits </span>' +
	'</div>';
	this.disabledEditContextMenuDiv = '<div class="contextMenu" id="ectMenuEditDisabled"> ' + this.disabledMessage + ' </span></div>';
	this.browseTInitContextMenuDiv = '<div class="contextMenu" id="ectMenuBrowseTInit">' +
	'   <span class="enableEditing"> Editing disabled while viewing tree w/o edits. </span>' +
	'</div>';
	this.emptyClusterWarning = '<span id="ectEmptyClusterWarning" class="error">This cluster is empty and will not be shown ' + 
	'in a re-built tree unless one or more result nodes are added to it before ' +
	're-isuing the query.</span>';
	
	/**
	 * Timer object used to time algo execution
	 */
	this.timer = function() {
		
		this.currentTime = 0;
		
		this.time = 0;
		
	    this.startTimer = function(){
	        d = new Date();
	        this.currentTime  = d.getTime();
	    };

	    this.stopTimer = function(){
	        d = new Date();
	        this.time = d.getTime() - this.currentTime;
	        this.time = Math.round(this.time/10)/100;
	        return this.getTime();
	    };
	    
	    this.addTime = function(duration){
	    	var t = duration.split(' ');
	    	if(t.length < 2){
	    		this.time += parseFloat(duration);
	    		return;
	    	}
	    	if(t[1] == 's')
	    		this.time += parseFloat(duration);
	    	else if(t[1] == 'ms')
	    		this.time += (parseFloat(duration) / 1000);
	    };
	    
	    this.subtractTime = function(duration){
	    	var t = duration.split(' ');
	    	if(t.length < 2){
	    		this.time -= parseFloat(duration);
	    		return;
	    	}
	    	if(t[1] == 's')
	    		this.time -= parseFloat(duration);
	    	else if(t[1] == 'ms')
	    		this.time -= (parseFloat(duration) / 1000);
	    };
	    
	    this.getTime = function(){
	    	return Math.round(this.time*1000)/1000 + ' s';
	    };
	};
	this.execTimer = new this.timer();
	this.presentationTimer = new this.timer();
	
	//root node for the internal cluster tree
	this.root = null;
	//jQuery select objects for the root node, display tree, results div
	var ulTree = null;
	var liRoot = null;
	var resDiv = $('#' + this.resultsDivElementId);
	/**
	 * results data object received from server.  Includes:
	 * - dataSourceKey
	 * - clusterKey
	 * - errors
	 * - returnedCount
	 * - totalResults
	 * - firstPosition
	 * - service
	 * - dataSourceName
	 * - results - array of results received from search engine.  Each result includes:
	 * 				- id
	 * 				- title
	 * 				- clickUrl
	 * 				- snippet
	 * 				- url
	 *              - trecId
	 *              - relevant
	 */
	this.results = null;
	this.customResultLabels = {}; //data store for label prefixes for duplicate label results
	this.maxPathFieldSize = 300; //maximum length of a path field in the db
	this.pagesIndex = {};        //keep track of clusters pages are in
	this.maximumDepth = 4;       //how deep the tree can go
	this.nextClusterId = 1;
	this.displayedPagesNodePath = '';  //the path of the node who's pages are 
                                 //currently being displayed in the results section
	this.mousePressed = false;   //whether mouse was pressed - used by drag-and-drop
	this.dragSource = null;      //parent of item being dragged
	this.dragDestination = null; //item dragging to
	this.dragCloneItem = null;   //item being dragged
	this.dragSetting = false;    //original setting of dragAllowed while draging is temporarily disabled
	this.dragCounter = 0;        //counter ensures drag actually occured due to browsers not always firing mouseup events when they should
	this.activeNode = null;      //display tree node actively selected for copy/cut
	this.activeNodeOp = 'copy';  //node operation
	this.activePage = null;      //page id that is copied/cut
	this.activePageNodePath = ''; //path of node page is displayed from when copied
	this.activePageOp = 'copy';  //page operation
	this.activeLabelPaths = [];  //label paths for currently completed operation
	this.activeRenameLabel = null; //label of item being renamed
	this.activeQueryParams = {}; //parameters for the executed query being displayed in the page
	this.tagItems = {};          //items in the tree that have been tagged
	this.tagCount = 0;           //number of tags to be set
	this.tagSelectPaths = [];    //paths that have been selected when tagging is enabled
	this.tagVisitOnlyPathsRate = 0.5; //percentage of results within visit only paths to count towards user effort
	this.tagExecutionInfo = {};  //when tagging is enabled, keeps stats after every 5 tagged items
	this.executionTimes = null;  //when testing, we need to keep a copy of execution times
	
	/////////////////////////////////////
	//                                 //
	//  LabelPath object methods       //
	//                                 //
	/////////////////////////////////////	
	
	/**
	 * The labelPath object allows tracking path changes that must be sent to the server
	 * after each operation
	 * @param path Array of node label strings representing a path
	 * @param sign +1 or -1, representing an added or subtracted path
	 */
	this.labelPath = function(path, sign){
		
		this.cardinality = 0;      //+1 for path addition, -1 for path subtraction
		this.lPath = [];         //array of labels constituting the path
		
		this.toString = function(){
			return this.cardinality > 0 ? '+ ' + this.lPath.join(' >> ') : 
				'- ' + this.lPath.join(' >> ');
		};
		
		if(typeof path.length === "number") this.lPath = path.slice(0);
		if(typeof sign == "undefined" || sign > 0)
			this.cardinality = 1; 
		else 
			this.cardinality = -1;
	};
	
	/**
	 * JSON representation of the path changes for current tree operation
	 * @return JSON String representation of the active label paths
	 */
	this.jsonLabelPathSet = function(){
		return $.toJSON( this.activeLabelPaths );
	};
	
	/**
	 * Clear label path changes and optional display for label path changes
	 * @return void
	 */
	this.clearLabelPathChanges = function(){
		this.activeLabelPaths = [];
		if(this.editLogElementId && $('#' + this.editLogElementId).length != 0)
			$('#' + this.editLogElementId).text('');
	};
	
	/**
	 * Display path changes in browser or console, only if cluster edits div is
	 * showing or debug is enabled.
	 * @return void
	 */
	this.showLabelPathChanges = function(){
		if(this.editLogElementId && $('#' + this.editLogElementId).length != 0){
			if($('#' + this.editLogElementId).is(':visible'))
				$('#' + this.editLogElementId).text( this.activeLabelPaths.join("\n") );
		} else 
			this.debugMessage( this.activeLabelPaths.join("\n") );
	};
	
	
	/**
	 * Clear timer messages
	 * @return void
	 */
	this.clearTimerMessages = function(){
		this.activeLabelPaths = [];
		if(this.timersElementId && $('#' + this.timersElementId).length != 0)
			$('#' + this.timersElementId).text('');
		this.execTimer.startTimer();
	};
	
	/**
	 * Display timer messages.
	 * @return void
	 */
	this.showTimerMessages = function(timers){
		if(typeof timers == "undefined") timers = {};
		timers['totalResponseTime'] = this.execTimer.stopTimer();
		timers['presentation'] = this.presentationTimer.stopTimer();
	
		var totalExecutionTime = new this.timer();
		var other = new this.timer();
		other.addTime(timers['totalResponseTime']);
		
		if('search' in timers){
			var t = 'Retrieving results: ' + timers['search'] + "\n";
			other.subtractTime(timers['search']);
		}
		if('preprocessing' in timers){
			t += 'Preprocessing: ' + timers['preprocessing'] + "\n";
			totalExecutionTime.addTime(timers['preprocessing']);
		}
		if('clustering' in timers){
			t += 'Initial clustering: ' + timers['clustering'] + "\n";
			totalExecutionTime.addTime(timers['clustering']);
		}
		if('preferences' in timers){
			t += 'Applying preferences: ' + timers['preferences'] + "\n";
			totalExecutionTime.addTime(timers['preferences']);
		}
		t += 'Presenting final tree: ' + timers['presentation'] + "\n";
		totalExecutionTime.addTime(timers['presentation']);
		other.subtractTime(totalExecutionTime.getTime());
		t += 'Other: ' + other.getTime() + "\n";
		t += 'Total execution time: ' + totalExecutionTime.getTime() + "\n";
		t += 'Total response time: ' + timers['totalResponseTime'] + "\n\n";
		
		timers['totalExecutionTime'] = totalExecutionTime.getTime();
		timers['other'] = other.getTime();
		this.executionTimes = this.clone( timers );
		//display timers if necessary
		if(this.timersElementId && $('#' + this.timersElementId).length != 0){
			$('#' + this.timersElementId).text( t );
		}
		
	};
	
	/**
	 * Get reference to the execution times object
	 */
	this.getExecutionTimes = function(){
		return this.executionTimes != null ? this.executionTimes : {};
	};
	
	/**
	 * get a copy of the internal tree as JSON strucure
	 * to store during testing
	 */
	this.getInternalStructure = function(){
		return this.root != null ? this.root.toJSON() : '{}';
	};
	
	/**
	 * Modify a set of paths by replacing the fromPath beginning portion of each path
	 * with the toPath given path.  The pathSet is a simple array or arrays of strings.
	 * FromPath and toPath are also array of strings.  FromPath can also just be length of
	 * fromPath, thus a number.
	 * @param pathSet Array of array of node labels
	 * @param fromPath Parent path included in each of the paths in pathSet that is
	 *        being changed.  Optionally the path length can be sent instead of the path
	 * @param toPath Array of node labels (path) that should be replaced in the pathSet paths
	 * @return pathSet with modified paths
	 */
	this.changePathInPathSet = function(pathSet, fromPath, toPath){
		var fromPathLength = typeof fromPath == "object" ? fromPath.length : fromPath;
		pathSet = pathSet.slice(0); //make copy of path set
		for(i = 0; i < pathSet.length; i++){
			var path = pathSet[i];
			for(j = 0; j < fromPathLength - 1; j++)
				path.shift();
			for(k = toPath.length - 1; k > -1; k--)
				path.unshift(toPath[k]);
			pathSet[i] = path.slice(0);
			path = null;
		}
		fromPathLength = null;
		return pathSet;
	};
	
	
	/////////////////////////////////////
	//                                 //
	//  Display tree related methods   //
	//                                 //
	/////////////////////////////////////
	
	
	/**
	  * Log message to Browser console
	  *
	  * @param txt Text to be written to console
	  * @return void
	  */
	this.logToConsole = function (txt){
	    if (window.console && window.console.log)
		  	window.console.log(txt);
	};
	  
	
	/**
	  * Clone an object hierarchy recursively
	  *
	  * @param: obj Object to be cloned
	  * @return void
	  */
	this.clone = function clone(obj){
	    if(obj == null || typeof(obj) != 'object')
	        return obj;
	  
	    return jQuery.extend(true, {}, obj);
	};
	
	/**
	 * Push element in array only if it does not already exit
	 * in the array
	 * @param array Array to push element into
	 * @param elem Element to push into array
	 * @return array after element was pushed, if not already present in array 
	 */
	this.uniqueArrayPush = function(array, elem){
		for(i in array)
			if(array[i] == elem) return array;
		array.push(elem);
		return array;
	};
	
	/**
	 * Return a set of unique elements from a given array
	 * @param array Array to be turned into set
	 * @param sorted Whether array is sorted already
	 * @param sortFunction Optional function to be used when sorting array
	 * @return array sorted and having had duplicate elements removed
	 */
	this.getArraySet = function(array, sorted, sortFunction){
		if(typeof array != "object" || typeof array.length !== "number") return false;
		if(array.length < 2) return array;
		//if not already sorted, sort, using optional sortFunction
		if(typeof sorted != "undefined" && !sorted)
			if(typeof sortFunction == "function")
				array.sort(sortFunction);
			else
				array.sort();
		
		var a = new Array();
		var last = null;
		for(i in array){
			if(array[i] != last) a.push(array[i]);
			last = array[i];
		}
		return a;
	};
	
	/**
	 * Allow changing the edit mode
	 * @param mode Whether edit mode is enabled or not
	 * @return void
	 */
	this.setEditAllowed = function(mode){
		this.editAllowed = mode == true;
	};
	
	/**
	 * Allow changing the disabled message
	 */
	this.setDisabledMessage = function(message){
		var m1 = this.disabledMessage;
		this.disabledMessage = message;
		this.disabledEditContextMenuDiv = this.disabledEditContextMenuDiv.replace(m1, message);
		$('#ectMenuEditDisabled').html( $('#ectMenuEditDisabled').html().replace(m1, message) );
		m1 = null;
	};
	
	/**
	 * Whether to allow tagging or not
	 * @param mode Whether tagging is enabled or not
	 * @return void
	 */
	this.enableTagging = function(mode){
		this.taggingEnabled = mode == true;
	};
	
	
	/**
	 * show stats message
	 * @param message Message to display
	 * @return void
	 */
	this.showStats = function(message){
		setDivMessage(this.statsDivElementId, message);
	};
	
	/**
	 * Show a message in the results portion of the page
	 * @param message Message to display
	 * @param cls CSS Class to be assigned to displayed message
	 * @return void
	 */
	this.showResultsMessage = function(message, cls){
		setDivMessage(this.resultsDivElementId, message, null, cls);
	};
	
	/**
	 * Show an error in the tree message portion of the page
	 * Depends on jQuery scrollTo plug-in
	 * @param message Message to display
	 * @param cls CSS Class to be assigned to displayed message
	 * @return void
	 */
	this.showTreeMessage = function(message, cls){
		setDivMessage(this.treeMessageElementId, message, 5000, cls);
		if(typeof cls != "undefined" && cls == 'error'){
			//in case of error force user's attention
			//to div message
			if($.scrollTo) $.scrollTo($('#' + Tree.treeMessageElementId),  {offset:-250});
		}
	};
	
	/**
	 * Show debug messages in appropriate context
	 * @param message Message to display
	 * @return void
	 */
	this.debugMessage = function (message){
		if(typeof message == "object"){
			if(message.length) //array
				message = message.join(', ');
			else { //object
				var s = '';
				for(i in message)
					s += 'key: ' + i + ' .. value: ' + message[i] + "\n";
				message = s;
				s = null;  //release memory
			}
		}
		
		if(this.debug == true)
			if(this.debugDivElementId && $('#' + this.debugDivElementId).length != 0)
				$('#' + this.debugDivElementId).text(message);
			else 
				this.logToConsole(message + "\n");
	};

	/////////////////////////////////////
	//                                 //
	//  Internal tree representation   //
	//                                 //
	/////////////////////////////////////
	
	/**
	 * Internal representation of a cluster node.  nodeData is received as JSON from
	 * server and includes:
	 * - id - string
	 * - level - int
	 * - pages - array of indexes within the results array
	 * - path - array of cluster ids (strings)
	 * - children - array of nodeData objects
	 * - label - string
	 * - parent_id - string parent id
	 * nodeData is also created with clone function
	 * if copy is set to true, a copy node is created, assigning new id, and computing path
	 * and level in relation to parent reference.
	 */
	this.node = function(parent, nodeData, copy){
		this.id = -1;
		this.level = -1;
		this.pages = null;
		this.path = new Array();
		this.addedPath = false;
		this.addedPages = null;
		this.label = '';
		this.parent = parent;
		this.children = null;
		this.childrenIndex = {};
		this.maxDepth = 0;
		
		//getters and setters
		this.getId = function() {
			return this.id;
		};
		this.setId = function(id){
			this.id = id;
		};
		this.getLevel = function() {
			return this.level;
		};
		this.setLevel = function(level){
			this.level = level;
		};
		this.getPages = function() {
			return this.pages;
		};
		this.getPath = function() {
			return this.path.slice(0);
		};
		this.setPath = function(path){
			this.path = path.slice(0);
		};
		this.getLabel = function() {
			return this.label;
		};
		this.setLabel = function(label){
			this.label = label;
		};
		this.getParent = function() {
			return this.parent;
		};
		this.setParent = function(parent){
			this.parent = parent;
		};
		this.getChildren = function() {
			return this.children;
		};
		this.setChildren = function(children){
			if(children && typeof children.length === "number")
				this.children = children.slice(0);
		};
		this.getMaxDepth = function() {
			return this.maxDepth;
		};
		this.setMaxDepth = function(maxDepth){
			this.maxDepth = maxDepth;
		};
		
		//internal node related methods
		
		/**
		 * JSON representation of node
		 * Depends on jquery.json-2.2.min.js
		 * @return JSON string representation of the node
		 */
		this.toJSON = function(){
			return $.toJSON(this.parentLess());
		};
		
		/**
		 * Internal method needed by clone and toJSON to remove circular references
		 * between parent and children nodes
		 * @return Object containing data for node and children without methods and
		 *         references to parent nodes
		 */
		this.parentLess = function(){
			var o = {
				id: this.id + "",
				level: this.level,
				pages: this.pages,
				path: this.path,
				label: this.label,
				maxDepth: 0,
				children: null
			};
			if(this.children != null){
				o.children = new Array();
				for(c in this.children)
					o.children[o.children.length] = this.children[c].parentLess();
			}
			return o;
		};
		
		/**
		 * Clone node
		 * @param parent Parent node
		 * @param copy Whether a copy of the node is being made
		 * @return new Tree node object with same data as current node
		 */
		this.clone = function(parent, copy){
			if(typeof copy == "undefined") copy = false;
			if(typeof parent == "undefined") parent = this.parent;
			return new Tree.node(parent, this.parentLess(), copy);
		};
		
		/**
		 * Get the id of this node's parent
		 * @return parent node id
		 */
		this.getParentId = function(){
			return this.parent != null ? this.parent.getId() : '';
		};
		
		/**
		 * Get the size of the cluster as a measure of all pages (result nodes)
		 * contained in the current cluster and/or all sub-clusters
		 */
		this.getSize = function(){
			if(this.children != null){
				var s = 0;
				for(c in this.children)
					s += this.children[c].getSize();
				return s;
			} else 
				return this.pages == null ? 0 : this.pages.length;
		};
		
		/**
		 * Get count of unique result nodes (pages) in this and all child clusters
		 * @return count of all unique pages in this node and/or children
		 */
		this.getUniquePageCount = function(){
			var allPages = this.getChildPages(); //pages already sorted
			var prev = -1;
			var count = 0;
			for(i in allPages){
				if(allPages[i] != prev) count++;
				prev = allPages[i];
			}
			allPages = null;
			prev = null;
			return count;
		};
		
		
		
		/**
		 * Get string representation of the path
		 * @return String representation of the node's path
		 */
		this.getPathString = function(){
			return this.path.join(".");
		};
		
		/**
		 * Get label as displayed in the page, including node size
		 * @return display label
		 */
		this.getDisplayLabel = function(){
			return this.getLabel() +'   (' + this.getUniquePageCount() + ')';
		};

		/**
		 * Get a node at given path from root
		 * Returns null if node does not exit
		 * @param path Node path starting with root for node looked for
		 * @return mixed Node found at path or null if node not found
		 */
		this.getPathNode = function(path){
			if(typeof path == "string") path = path.split(".");
			//path should be array of string ids and always start at root
			if(typeof path.length !== "number" || path.length < 1 || path[0] != "0") return null;
			var n = Tree.root;
			for(i = 1; i < path.length; i++){
				n = n.getChild(path[i]);
				if(n == null) return n;
			}
			return n;
		};
		
		/**
		 * Retrieve path as array of labels to node
		 * @param path Node path starting with root
		 * @param pageId Result node index that path should end at
		 * @return array of node labels along path
		 */
		this.getLabelPath = function(path, pageId){
			if(typeof path == "undefined") path = this.path;
			if(typeof path == "string") path = path.split('.');
			//path should be array of string ids and always start at root
			if(typeof path.length !== "number" || path.length < 1 || path[0] != "0") return [];
			var n = Tree.root;
			var p = new Array();
			p[p.length] = n.getLabel();
			for(i = 1; i < path.length; i++){
				n = n.getChild(path[i]);
				if(n != null) p[p.length] = n.getLabel();
			}
			//if requesting path all the way to specific page
			if(typeof pageId != "undefined" && n.hasPage(pageId)){
				p[p.length] = Tree.getResultNodeLabel(pageId);
			}
			n = null;
			return p;
		};
		
		/**
		 * array of indexes of child nodes along the path.
		 * the last elem of the array is the index of the result within the
		 * base cluster results array.  indexes are 1 based
		 * @param path Node path starting with root
		 * @param pageId Result node index that path should end at
		 * @return array of node 1-based indexes within level along path
		 */
		this.getChildIndexPath = function(path, pageId){
			if(typeof path == "undefined") path = this.path;
			if(typeof path == "string") path = path.split('.');
			//path should be array of string ids and always start at root
			if(typeof path.length !== "number" || path.length < 1 || path[0] != "0") return [];
			var n = Tree.root;
			var p = new Array();
			p[p.length] = 0;  //no cost for root
			for(var i = 1; i < path.length; i++){
				var level = n.getChildren();
				if(level == null) break; //bottom node has been reached
				for(var j = 0; j < level.length; j++){
					if(level[j].id == path[i]){
						p[p.length] = j + 1;
						break;
					}
				}
				n = n.getChild(path[i]);
			}
			//now get index of result within pages array
			if(typeof pageId != "undefined"){
				if(path.length == 1){ //root node selected, thus pageId is result index
					p[p.length] = parseInt(pageId) + 1;
				} else {
					var pages = n.getChildPages();
					pages = Tree.getArraySet(pages, true, function(a,b){return a-b;}); //make set of pages and sort
					for(var j = 0; j < pages.length; j++){
						if(pages[j] == pageId)
							p[p.length] = j + 1;
					}
				}
			}
			n = null;
			return p;
		};
		
		/**
		 * Retrieve array of sub-paths from current node to all leaf nodes (pages)
		 * 
		 * Optimized algorithm travels only downward, passing a reference of the
		 * set and path arrays.  Each node makes a copy of the path array, populates
		 * its label within it, and forwards the call to children.  If no children,
		 * it adds individual paths for all contained pages to the set array.
		 * 
		 * @param set Array of label paths populated by all children of current node
		 * @param path Current path sent of parent node
		 * @return label path set
		 */
		this.getLeafLabelPathSet = function(set, path){
			if(typeof set == "undefined") set = new Array();
			var p, resultPages;
			if(typeof path == "undefined"){
				p = this.getLabelPath(this.path);
			} else {
				p = path.slice(0);
				p.push(this.getLabel());
			}
			
			if(this.children != null){
				for(c in this.children){
					this.children[c].getLeafLabelPathSet(set, p.slice(0));
				}
			} else if(this.pages != null){
				resultPages = Tree.results.results;
				for(i in this.pages){
					var page = p.slice(0); //page path
					var label = Tree.getResultNodeLabel( this.pages[i] );
					page.push(label);
					set.push(page);
					page = null; //release memory
					label = null;
				}					
			}
			p = null;  //release memory
			pages = null;
			
			return set;
		};
		
		/**
		 * Get reference to a child node
		 * @param childId Node id of the child node looked for
		 * @return mixed Child node if found, null otherwise
		 */
		this.getChild = function(childId){
			if(this.children == null) return null;
			if("c" + childId in this.childrenIndex)
				return this.children[ this.childrenIndex[ "c" + childId] ];
			return null;
		};
		
		/**
		 * Retrieve all pages for all subtrees recursively
		 * @param cn Whether this is a child node recursive call, thus should not sort results
		 * @return array of pages in this node and all children
		 */
		this.getChildPages = function(cn){
				
			var pa = new Array();
			
			if(this.children != null){
				for(c in this.children)
					pa = pa.concat( this.children[c].getChildPages(false));
			} else {
				if(this.pages != null)
					pa = pa.concat(this.pages);
			}
			//if calling node, sort result
			return typeof cn != "undefined" ? pa : pa.sort(function(a,b){return a-b;});
		};
		
		/**
		 * Get all pages for this and child nodes that were added through previous edits
		 */
		this.getAddedPages = function(cn){
			var pa = new Array();
			
			if(this.children != null){
				for(c in this.children)
					pa = pa.concat( this.children[c].getAddedPages(false));
			} else {
				if(this.addedPages != null)
					pa = pa.concat(this.addedPages);
			}
			//if calling node, sort result
			return typeof cn != "undefined" ? pa : pa.sort(function(a,b){return a-b;});
		};
		
		/**
		 * Check if node already contains a given page
		 * @param pageId Result node page index to be searched for
		 * @return boolean Whether node was found
		 */
		this.hasPage = function(pageId){
			if(this.pages != null)
				for(p in this.pages)
					if(pageId == this.pages[p])
						return true;
			return false;
		};
		
		/**
		 * Check whether node has pages
		 * @return boolean
		 */
		this.hasPages = function(){
			this.pages != null && this.pages.length > 0;
		};
		
		/**
		 * Check whether node has children
		 * @return boolean
		 */
		this.hasChildren = function(){
			return this.children != null;
		};
		
		/**
		 * Check if node has a child with a given label
		 * @param label to be looked for
		 * @return boolean
		 */
		this.hasChildWithLabel = function(label){
			label = label.toLowerCase();
			for(c in this.children)
				if(this.children[c].getLabel().toLowerCase() == label)
					return true;
			return false;
		};
		
		/**
		 * Check whether page can be deleted from this node and/or its children
		 */
		this.canRemovePage = function(pageId){
			//page must have at least one other copy in the cluster outside of this 
			//node and its sub-cluster
			var index = Tree.pagesIndex["p" + pageId];
			var count = 0;
			var path;
			var displayPath = this.getPathString() + '.'; 
			
			//count paths in page index that are not children of selected path
			for(i in index){
				path = index[i] + '.';
				if(path.indexOf(displayPath) == -1) count++;
			}
		    pageId = null;  //release memory
		    index = null;
		    path = null;
		    displayPath = null;
		    //at least one page not in this path will be left after delete
		    return count > 0;
		};
		
		/**
		 * Update the path in this node and its children
		 * @param path - path of parent node that child is being added to
		 *               or updated path of child (parent + child id)
		 * @param parentLevel - level of parent node
		 * @return void
		 */
		this.updatePathAndLevel = function(path, parentLevel){
			this.level = parentLevel + 1;
			if(typeof path === "string")
				path = path.split('.');
			path = path.slice(0);
			if( path[path.length - 1] != this.id)
				path.push(this.id); 
			if(this.hasPages()){
				var strPath = path.join('.');
				var currentStrPath = this.path.join('.');
				//update pages index
				for(p in this.pages){
					var index = [];
					var pid = "p" + this.pages[p];  //string page id
					if(pid in Tree.pagesIndex)
						index = Tree.pagesIndex[pid];
					for(i in index)
						if(index[i] == currentStrPath) index[i] = strPath;
					Tree.pagesIndex[pid] = index;
					index = null;
					pid = null;
				}
				currentStrPath = null;
				strPath = null;
			}
			this.path = path;
			
			if(this.children != null)
				for(c in this.children)
					this.children[c].updatePathAndLevel(this.path.slice(0), this.level);
		};
		
		/**
		 * Update maxDepth information after a node change
		 * Travels upward on path but stops prematurely if 
		 * conditions warrant stop
		 * @param d Current node maxDepth to set
		 * @return void
		 */
		this.updateMaxDepth = function(d){
			//check if max depth is decreasing
			var decrease = this.maxDepth > d;
			this.maxDepth = d;
			var p = this.parent;
			var i = 1;
			var parentMaxDepth;
			while(p != null){
				parentMaxDepth = p.getMaxDepth();
				if(parentMaxDepth < d + i) {
					//path has increased in size - most often
					p.setMaxDepth(d + i++);
				} else if(parentMaxDepth > d + i) {
					//path has decreased in size - we must check parent's other children
					//to see if parent's depth must be decreased 
					var maxD = 0;
					var children = p.getChildren();
					for(c in children) {
						var md = children[c].getMaxDepth();
						if(md > maxD) maxD = md;
					}
					maxD += 1;  //parent is one level up from children
					if(maxD == parentMaxDepth){
						//other children dictated larger maxDepth
						//parent path does not need to be changed
						break;
					} else if(maxD < parentMaxDepth){
						if(maxD < d + i) maxD = d + i++;
						p.setMaxDepth(maxD);
					}
					maxD = null;
					children = null;
				} else {
					//parent path is accurate.  we do not need to continue checking parents
					break;
				}
				p = p.getParent();
			}
			i = null;
			parentMaxDepth = null;
			decrease = null;
			p = null;
		};
		
		/**
		 * Add a result node (page) to this cluster
		 * @param pageId The result node index to be added
		 * @return Node result node (page) was added to
		 */
		this.addPage = function(pageId){
			if(this.pages == null) this.pages = new Array();
			this.pages.push(parseInt(pageId));  //this.pages is array of integers
			//update pages index
			var index = [];
			var pid = "p" + pageId;  //string page id
			if(pid in Tree.pagesIndex)
				index = Tree.pagesIndex[pid];
			index = Tree.uniqueArrayPush(index, this.getPathString());
			Tree.pagesIndex[pid] = index;
			
			index = null; //release memory
			pid = null;
			
			return this;
		};
		
		/**
		 * Remove a result node (page) from this cluster
		 * @param pageId The result node index to be removed
		 * @return mixed Current node method was called on or 
		 * 				 false if page not found in current node
		 */
		this.removePage = function(pageId){
			//if internal node, pass call unto children
			var found = false;
			if(this.children != null){
				for(c in this.children)
					if(this.children[c].removePage(pageId) !== false)
						found = true;
				return found == true ? this : found;
			}
			if(this.pages != null)
				for(i in this.pages)
					if(this.pages[i] == pageId){
						this.pages.splice(i, 1);
						found = true;
					}
			if(found){
				var pid = "p" + pageId;  //string page id
				var index = Tree.pagesIndex[pid];
				var path = this.getPathString();
				if(typeof index == "object"){
					for(i in index)
						if(index[i] == path)
							index.splice(i, 1);
					Tree.pagesIndex[pid] = index;
				}
				pid = null; //release memory
				index = null;
				path = null;
				
				return this;
			}
			found = null; //release memory
			
			return false;
		};
		
		/**
		 * Remove all pages from this node and its children and 
		 * update pages index
		 * @return void
		 */
		this.removeAllPages = function(){
			if(this.hasPages()) {
				var path = this.getPathString();
				for(p in this.pages){
					var pid = "p" + this.pages[p];
					var index = Tree.pagesIndex[pid];
					if(typeof index == "object"){
						for(i in index)
							if(index[i] == path)
								index.splice(i, 1);
						Tree.pagesIndex[pid] = index;
					}
					pid = null; //release memory
					index = null;
				}
				path = null;
				this.pages = null;
			}
			//recursive call to allow removing all children pages from upper level path node
			if(this.children != null){
				for(c in this.children)
					this.children[c].removeAllPages();
			}
		};
		
		/**
		 * Remove a child
		 * @param childId Node id of the child node to be removed
		 * @return mixed Parent node child was removed from, false if child not found
		 */
		this.removeChild = function(childId, move){
			if(typeof move == "undefined") move = false;
			if(!childId in this.childrenIndex) return false;
			//child index
			var ci = this.childrenIndex["c" + childId];
			//first remove pages from index unless this is a move operation
			if(!move)
				this.children[ ci ].removeAllPages();
			
			//update higher indexes in the childrenIndex and decrement value
			for(i in this.childrenIndex)
				if(this.childrenIndex[i] > ci) this.childrenIndex[i]--;
			//remove child object and its index
			this.children.splice(ci, 1);
			delete this.childrenIndex["c" + childId];
			
			ci = null;  //release memory
			
			//turn into bottom-node
			if(this.children.length == 0) this.children = null;
			
			//update maxDepth and parent's maxDepth
			var d = 0;  //max children maxDepth
			var d2;
			if(this.children != null){
				for(c in this.children){
					d2 = this.children[c].getMaxDepth();
					if(d2 > d)
						d = d2;
				}
				d += 1; //current depth is one level up
			}
			if(d < this.maxDepth)
				this.updateMaxDepth(d);
			
			d = null;  //release memory
			d2 = null;
			
			return this;
		};
		
		/**
		 * Add a child
		 * 
		 * If current node is bottom node, it is automatically 
		 * transformed to internal node and child is added.  MaxDepth and
		 * levels are updated up the path as well as within children of n.
		 * If child does not yet have a set id, the next available id is
		 * assigned to child node.
		 * 
		 * @param n Node to be added as child of current node
		 * @return Reference to node that was added.
		 */
		this.addChild = function(n){
			if(this.children == null)
				this.children = new Array();
			//get and validate child id
			var id = n.getId();
			if(id < 0){
				id = Tree.nextClusterId;
				n.setId(id);
			}
			//assign parent to child
			n.setParent(this);
			//assign path and level to child and its children
			n.updatePathAndLevel( this.path.slice(0), this.level );
			//add child to node
			this.childrenIndex["c" + id] = this.children.length;
			this.children[this.children.length] = n;
			//update nextClusterId if necessary
			try {
				id = parseInt(id);
				if(!isNaN(id) && Tree.nextClusterId <= (id))
					Tree.nextClusterId = id + 1;
			} catch (Exception){
				Tree.nextClusterId++;
			};
			//update maxDepth and parents' maxDepth
			var d = n.getMaxDepth() + 1;
			if(d > this.maxDepth)
				this.updateMaxDepth(d);
			id = null;
			d = null;
			return n;
		};
		
		/**
		 * String representation of a node
		 * @return String representation of node and its children, used in debugging
		 */
		this.toCustomString = function(){
			var s = 'id: ' + this.id + "\n" +
			'level: ' + this.level + "\n" +
			'pages: ' + this.pages + "\n" +
			'path: ' + this.path + "\n" +
			'label: ' + this.label + "\n" +
			'size: ' + this.getSize() + "\n" +
			'maxDepth: ' + this.maxDepth + "\n" +
			'parent_id: ' + this.getParentId() + "\n";
			
			if(this.children != null)
				for(c in this.children)
					s += "\n    " + this.children[c].toCustomString().replace(/\n/g, "\n    ");
			return s;
		};
		
		/**
		 * Get JSON representation of the internal tree
		 */
		this.treeToJSON = function(){
			return this.root.toJSON();
		};
		
		/**
		 * Build HTML representation of the tree that will
		 * later be animated by the editableClusterTree
		 * @param last Whether node is bottom node
		 * @return HTML string representing node and its children
		 */
		this.toHTML = function(last) {
			if(this.children == null && this.pages == null && this.label == '')
				return ''; //empty cluster
			var lastStr = typeof last == "undefined" || !last ? '' : '-last';
			var s = '';
			if(this.level == 0){
				//root node
				s += '<ul id="ecTreeUl" class="ecTree">'; //start tree
				s += '<li id="ecTreeRoot" name="0" class="root">'; //start root elem
				s += '<span class="label">' + this.getDisplayLabel() + '</span>'; //add size
				
			} else {
				var nodeClass = this.children != null ? 
						'cluster-closed' : 'bottom-node';
				
				s += '<li name="' + this.getPathString() + 
					'" class="' + nodeClass + lastStr + '">'; //start bottom elem
				s += '<span class="label">' + this.getDisplayLabel();
				if(this.addedPath == true) 
					s += '  <span class="addedPath" title="Previously edited cluster node">*</span>';
				s += '</span>'; //add size
			}
			
			//process children
			if(this.children != null){
				s += '<ul>';
				var l = this.children.length;
				for(c in this.children)
					s+= c != l - 1 ? this.children[c].toHTML() : 
						this.children[c].toHTML(true);
				s += '</ul>';
			}
			
			s += '</li>'; //close node element
			
			if(this.level == 0)
				s += '</ul>'; //close tree
			
			lastStr = null;  //release memory
			
			return s;
		};
		
		/**
		 * Initializes node with data that may be passed on to it
		 */
		this.init = function(parent, nodeData, copy){
			notCopy = typeof copy == "undefined" ? true : !copy;
			//get unique id if id not already provided
			this.id = notCopy ? "" + nodeData.id : "" + Tree.nextClusterId++;
			//set level and path for root node
			if(parent == null) {
				this.level = 0;
				this.path = ["0"];
			} else {
				this.path = parent.getPath().slice(0);
				this.path.push(this.id);
			}
			if(nodeData.pages != null)
				for(i in nodeData.pages)
					this.addPage( nodeData.pages[i] );
			if(nodeData.addedPages != null)
				this.addedPages = nodeData.addedPages.slice(0);
			this.label = nodeData.label;
			if(typeof nodeData.addedPath != "undefined")
				this.addedPath = nodeData.addedPath; 
			//init children
			if(nodeData.children != null){
				this.children = new Array();
				for(c in nodeData.children){
					var n = new Tree.node(this, nodeData.children[c], copy);
					this.addChild(n);
					n = null;
				}
			}
		};
		
		if(typeof nodeData != "undefined")
			this.init(parent, nodeData, copy);
		
	};
	
	///////////////////////////////////////////
	//                                       //
	//  Internal tree modification methods   //
	//                                       //
	///////////////////////////////////////////
	
	/**
	 * Exception object to be used when executing tree operations
	 * @param message Exception message
	 * @param type Exception type
	 * @param stack Next exception in the stack or null
	 */
	this.ecTreeOpException = function(message, type, stack){
		this.message = message;
		this.type = typeof type != "undefined" ? type : null;
		this.stack = typeof stack != "undefined" ? stack : null;
		this.name = 'ecTreeOpException';
		this.getMessage = function(){ return this.message; };
		this.getStack = function(){ return this.stack; };
		this.toString = function(){
			var s = this.type == null ? this.name + ': "' + this.message + '"' : 
				this.name + '[' + this.type + ']: "' + this.message + '"';
			if(this.stack != null) s += "\n\t" + stack;
			return s;
		};
	};
	
	
	/**
	 * Get label for a given result node
	 * @param pageId Result node index
	 * @return result node label
	 */
	this.getResultNodeLabel = function(pageId){
		var pages = Tree.results.results;
		var label = '';
		if(pageId in pages)
			label = $.trim( pages[pageId]['url'] );
		if(pageId in this.customResultLabels)
			label = this.customResultLabels[pageId] + label;
		if(label.length > this.maxPathFieldSize)
			label = label.substring(0, this.maxPathFieldSize - 1);
		return label;
	};
	
	
	/**
	 * e0: create a label node as child of a non-bottom label node
	 */
	this.addNode = function(label, pathTo, id){
		var parent = this.root.getPathNode(pathTo);
		if(parent == null) 
			throw new this.ecTreeOpException('Destination node path is invalid.', 'addNode');
		//validate addition
		if(parent.hasPages())  //bottom level node
			throw new this.ecTreeOpException('Node cannot be added as destination node is a bottom node.', 'addNode');
		//check if node addition is valid
		if(parent.getLevel() + 1 > this.maximumDepth)
			throw new this.ecTreeOpException('Node cannot be added due to maximum tree depth constraint.', 'addNode');
		//check if node with same path already exists
		label = $.trim( label );
		if(label.length < 1)
			throw new this.ecTreeOpException('The node label cannot be empty.', 'addNode');
		if(parent.hasChildWithLabel(label))
			throw new this.ecTreeOpException('Node to be added already exists in destination node.', 'addNode');
		
		//no path changes when just adding a node		
		
		//perform addition
		if(typeof id == "undefined") id = this.nextClusterId;
		var node = new Tree.node(parent);
		node.setId(id);
		node.setLabel(label);
		return parent.addChild(node);
	};
	
	
	/**
	 * e1: copy a label node to another non-bottom label node
	 * as its child.
	 * The pathTo element must exist (new parent path)
	 */
	this.copyNode = function (pathFrom, pathTo){
		//get references to current objects
		var newParent = this.root.getPathNode(pathTo);
		if(newParent == null) 
			throw new this.ecTreeOpException('Destination node path is invalid.', 'copyNode');
		//validate move
		if(!newParent.hasChildren())  //bottom level node
			throw new this.ecTreeOpException('Destination node cannot be a bottom node.', 'copyNode');
		//node to copy must exist
		var node = this.root.getPathNode(pathFrom);
		if(node == null) 
			throw new this.ecTreeOpException('Source node path is invalid.', 'copyNode');
		//check if node with same path already exists
		if(newParent.hasChildWithLabel(node.getLabel()))
			throw new this.ecTreeOpException('Node to be copied already exists in destination node.', 'copyNode');
		//check if copy will bring maxDepth of root beyond this.maximumDepth
		if(node.getMaxDepth() + newParent.getLevel() + 1 > this.maximumDepth)
			throw new this.ecTreeOpException('Node copy invalid due to maximum tree depth constraint.', 'copyNode');
		
		//identify paths to change and send to server via AJAX
		this.activeLabelPaths = [];
		var initialPaths = node.getLeafLabelPathSet();
		var pf = node.getPath();
		var pt = newParent.getLabelPath();
		var changedPaths = this.changePathInPathSet(initialPaths, pf.length, pt);
		for(p in changedPaths)
			this.activeLabelPaths.push( new this.labelPath(changedPaths[p], +1) );
		
		this.showLabelPathChanges();

		//send path changes to server for permanent storage
		try {
			this.sendPathUpdates();
		} catch(e){
			throw new this.ecTreeOpException('Could not send path changes to the server: ' + e.getMessage(), 'moveNode', e);
		}
		
		//modify node internal references and data
		var newNode = node.clone(newParent, true);
		node = null;   //release memory
		
		//copy node
		return newParent.addChild(newNode);
	};
	
	/**
	 * e2: copy a result node to a bottom label node.
	 * both from and to nodes must exist and be bottom level nodes
	 */
	this.copyResult = function(pathFrom, pathTo, pageId){
		var nodeFrom = this.root.getPathNode(pathFrom);
		if(nodeFrom == null)
			throw new this.ecTreeOpException('Source node path is invalid.', 'copyResult');
		var nodeTo = this.root.getPathNode(pathTo);
		if(nodeTo == null) 
			throw new this.ecTreeOpException('Destination node path is invalid.', 'copyResult');
		//validate paste
		if(nodeTo.hasChildren())  //not bottom level node
			throw new this.ecTreeOpException('Destination node must be a bottom node.', 'copyResult');
		if(nodeTo.hasPage(pageId))
			throw new this.ecTreeOpException('Result node already exists in destination node.', 'copyResult');
		
		//identify paths to change and send to server via AJAX
		this.activeLabelPaths = [];
		var lpTo = this.root.getLabelPath(pathTo);
		lpTo.push( this.getResultNodeLabel(pageId) );
		//add new path
		this.activeLabelPaths.push( new this.labelPath(lpTo) );
		
		this.showLabelPathChanges();
		
		//send path changes to server for permanent storage
		try {
			this.sendPathUpdates();
		} catch(e){
			throw new this.ecTreeOpException('Could not send path changes to the server: ' + e.getMessage(), 'moveNode', e);
		}
		
		nodeFrom = null;  //release memory
		lpFrom = null;
		lpTo = null;
		
		//perform addition
		return nodeTo.addPage(pageId);
	};
	
	/**
	 * e3: modify a non-root label node.
	 */
	this.renameNode = function(path, label){
		var node = this.root.getPathNode(path);
		if(node == null) 
			throw new this.ecTreeOpException('Destination node rename is invalid.', 'renameNode');
		//validate renaming
		if(node.getParent() == null)
			throw new this.ecTreeOpException('Root node cannot be renamed.', 'renameNode');
		label = $.trim( label );
		if(node.getLabel().toLowerCase() == label.toLowerCase())
			throw new this.ecTreeOpException('The node label has not changed.', 'renameNode');
		if(label.length < 1)
			throw new this.ecTreeOpException('The node label cannot be empty.', 'renameNode');
		//check if node with same path already exists
		if(node.getParent() != null && node.getParent().hasChildWithLabel( label ))
			throw new this.ecTreeOpException('Another node exists with the same label path.', 'renameNode');
		
		//identify paths to change and send to server via AJAX
		this.activeLabelPaths = [];
		var initialPaths = node.getLeafLabelPathSet();
		for(p in initialPaths)
			this.activeLabelPaths.push( new this.labelPath(initialPaths[p], -1) );
		var level = node.getLevel();
		for(p in initialPaths){
			initialPaths[p][level] = label;
			this.activeLabelPaths.push( new this.labelPath(initialPaths[p], +1) );
		}
		this.showLabelPathChanges();
		
		//send path changes to server for permanent storage
		try {
			this.sendPathUpdates();
		} catch(e){
			throw new this.ecTreeOpException('Could not send path changes to the server: ' + e.getMessage(), 'moveNode', e);
		}
		
		//perform label change
		node.setLabel(label);
		
		initialPaths = null;
		level = null;
		return node;
	};
	
	
	/**
	 * e4-a: delete a non-root label node.
	 */
	this.removeNode = function(path){
		var node = this.root.getPathNode(path);
		if(node == null) 
			throw new this.ecTreeOpException('Destination node remove is invalid.', 'removeNode');
		//validate removal
		var parent = node.getParent();
		if(parent == null)
			throw new this.ecTreeOpException('Root node cannot be removed.', 'removeNode');
		
		//identify paths to change and send to server via AJAX
		this.activeLabelPaths = [];
		var initialPaths = node.getLeafLabelPathSet();
		for(p in initialPaths)
			this.activeLabelPaths.push( new this.labelPath(initialPaths[p], -1) );
		
		this.showLabelPathChanges();
		
		//send path changes to server for permanent storage
		try {
			this.sendPathUpdates();
		} catch(e){
			throw new this.ecTreeOpException('Could not send path changes to the server: ' + e.getMessage(), 'moveNode', e);
		}
		
		parent.removeChild(node.getId());
		node = null;  //release memory
		
		return parent;
	};
	
	/**
	 * e4-b: delete a result node (page).
	 */
	this.removeResult = function(path, pageId){
		var node = this.root.getPathNode(path);
		if(node == null) 
			throw new this.ecTreeOpException('Result node is invalid.', 'removeResult');
		
		//identify paths to change and send to server via AJAX
		this.activeLabelPaths = [];
		var lpFrom;
		var changedPaths = this.getResultNodeSubClusterPaths(pageId);
		if(changedPaths && changedPaths.length > 0)
			for(i in changedPaths){
				lpFrom = this.root.getLabelPath(changedPaths[i], pageId);
				//subtract deleted path
				this.activeLabelPaths.push( new this.labelPath(lpFrom, -1) );
			}
		this.showLabelPathChanges();
		
		//send path changes to server for permanent storage
		try {
			this.sendPathUpdates();
		} catch(e){
			throw new this.ecTreeOpException('Could not send path changes to the server: ' + e.getMessage(), 'moveNode', e);
		}
		
		lpFrom = null;
		changedPaths = null;
		
		//execute operation
		return node.removePage(pageId);
	};
	
	/**
	 * Move result from one node to another
	 */
	this.moveResult = function(pathFrom, pathTo, pageId){
		var nodeFrom = this.root.getPathNode(pathFrom);
		if(nodeFrom == null) 
			throw new this.ecTreeOpException('Source node path is invalid.', 'moveResult');
		var nodeTo = this.root.getPathNode(pathTo);
		if(nodeTo == null) 
			throw new this.ecTreeOpException('Destination node path is invalid.', 'moveResult');
		//validate removal
		if(nodeTo.hasChildren())  //bottom level node
			throw new this.ecTreeOpException('Destination node must be a bottom node.', 'moveResult');
		if(nodeTo.hasPage(pageId))
			throw new this.ecTreeOpException('Destination node already contains page.', 'moveResult');
		
		//identify paths to change and send to server via AJAX
		this.activeLabelPaths = [];
		var lpFrom;
		var changedPaths = this.getResultNodeSubClusterPaths(pageId);
		if(changedPaths && changedPaths.length > 0)
			for(i in changedPaths){
				lpFrom = this.root.getLabelPath(changedPaths[i], pageId);
				//subtract deleted path
				this.activeLabelPaths.push( new this.labelPath(lpFrom, -1) );
			}

		var lpTo = this.root.getLabelPath(pathTo);
		lpTo.push( this.getResultNodeLabel(pageId));
		//add new path
		this.activeLabelPaths.push( new this.labelPath(lpTo, +1) );
		this.showLabelPathChanges();
		
		//send path changes to server for permanent storage
		try {
			this.sendPathUpdates();
		} catch(e){
			throw new this.ecTreeOpException('Could not send path changes to the server: ' + e.getMessage(), 'moveNode', e);
		}
		
		lpFrom = null;
		lpTo = null;
		
		//execute operation
		try {
			nodeFrom.removePage(pageId);
			nodeTo.addPage(pageId);
		} catch (e){
			throw new this.ecTreeOpException('Could not complete result move: ' + e.getMessage(), 'moveResult', e);
		}
		nodeFrom = null;  //release memory
		nodeTo = null;
	};
	
	/**
	 * Move a node from one location in the tree to another
	 * The pathTo element must exist (new parent path)
	 */
	this.moveNode = function (pathFrom, pathTo){
		//get references to current objects
		var node = this.root.getPathNode(pathFrom);
		if(node == null)
			throw new this.ecTreeOpException('Source node path is invalid.', 'moveNode');
		var oldParent = node.getParent();
		if(oldParent == null) //can't move the root
			throw new this.ecTreeOpException('The root node cannot be moved.', 'moveNode');
		var newParent = this.root.getPathNode(pathTo);
		if(newParent == null) 
			throw new this.ecTreeOpException('Destination node path is invalid.', 'moveNode');
		//check if node with same path already exists in destination
		var sibs = newParent.getChildren();
		var label = node.getLabel();
		for(c in sibs)
			if(sibs[c].getLabel().toLowerCase() == label.toLowerCase())
				throw new this.ecTreeOpException('Node to be moved already exists in destination node.', 'moveNode');
		sibs = null;
		label = null;
		//check if move will bring maxDepth of root beyond this.maximumDepth
		if(node.getMaxDepth() + newParent.getLevel() > this.maximumDepth)
			throw new this.ecTreeOpException('Node move invalid due to maximum tree depth constraint.', 'moveNode');
		
		//identify paths to change and send to server via AJAX
		this.activeLabelPaths = [];
		var initialPaths = node.getLeafLabelPathSet();
		for(p in initialPaths)
			this.activeLabelPaths.push( new this.labelPath(initialPaths[p], -1) );
		var pf = node.getPath();
		var pt = newParent.getLabelPath();
		var changedPaths = this.changePathInPathSet(initialPaths, pf.length, pt);
		for(p in changedPaths)
			this.activeLabelPaths.push( new this.labelPath(changedPaths[p], +1) );
		
		this.showLabelPathChanges();
		
		//send path changes to server for permanent storage
		try {
			this.sendPathUpdates();
		} catch(e){
			throw new this.ecTreeOpException('Could not send path changes to the server: ' + e.getMessage(), 'moveNode', e);
		}
		
		//execute move
		try {
			var nodeId = node.getId();
			node.setParent(null);
			newParent.addChild(node);
			oldParent.removeChild(nodeId, true);
			nodeId = null;  //release memory
			oldParent = null;
			newParent = null;
			
			return node;
		} catch (e){
			throw new this.ecTreeOpException('Could not complete node move: ' + e.getMessage(), 'moveNode', e);
		}
		
		
	};
	

	/////////////////////////////////////
	//                                 //
	//  Display tree related methods   //
	//                                 //
	/////////////////////////////////////
	
	/**
	 * Open/Close a given node
	 */
	this.nodeToggle = function(obj, callback)
	{
		
		if(typeof obj.length === "number") //jQuery obj array
			obj = obj.get(0);
		
		var childUl = $('>ul', obj);
		
		if(childUl.is(':visible')) {
			obj.className = obj.className.replace('open', 'closed');
		} else {
			obj.className = obj.className.replace('closed', 'open');
		}
		childUl.animate({height: "toggle"}, Tree.animationSpeed);
		
		childUl = null;  //release memory
	};
	
	/**
	 * Open/Close all the sub-nodes of a given node
	 */
	this.subtreeToggle = function(obj,callback)
	{
		if (!$(obj).hasClass('root')){
			$('>ul', obj).children().each(function(){ 
				Tree.subtreeToggle(this); 
			});
		
			Tree.nodeToggle(obj); 
		} else {
			if($('>ul', obj).children().filter('.cluster-closed, .cluster-closed-last').length > 0){
				//if root and there are some closed children, expand all
				Tree.expandAll(obj);
			} else {
				//else collapse to first level
				$('>ul', obj).children().each(function(){ 
					Tree.collapseAll(this); 
				});
			}
		}
	};
	
	/**
	 * Close all nodes on a level
	 */
	this.closeLevel = function(obj)
	{
		$(obj).siblings().filter('.cluster-open, .cluster-open-last').each(function(){
			var childUl = $('>ul',this);
			var className = this.className;
			this.className = className.replace('open','closed');
			childUl.animate({height:"toggle"}, Tree.animationSpeed);
			childUl = null;  //release memory
			className = null;
		});
	};
	
	
	/**
	 * Expand all sub-nodes of the display tree given a node
	 */
	this.expandAll = function(obj){
		$('.cluster-closed, .cluster-closed-last', obj).each(function(){
			Tree.nodeToggle(this, Tree.expandAll);
		});
	};
	
	/**
	 * Collapse all nodes
	 */
	this.collapseAll = function(){
		$('.cluster-open, .cluster-open-last').each(function(){
			Tree.nodeToggle(this, Tree.collapseAll);
		});
	};
	
	/**
	 * Enable toggle for given node
	 */
	this.enableToggle = function(node){
		$('>span',node).before('<img class="trigger" src="includes/images/tree/spacer.gif" style="float: left" border="0">');
		$('>.trigger', node).click(function(event){
			Tree.nodeToggle(node);
		});
	};
	
	/**
	 * Get paths for page in sub-cluster from cluster with given path
	 * If path not given, path = this.displayedPagesNodePath
	 */
	this.getResultNodeSubClusterPaths = function(pageId, path){
		if(typeof path == "undefined") path = this.displayedPagesNodePath;
		path += '.';  //adding . at end to avoid 
		//issue with comparison of something like 0.1 with 0.14
		var index = Tree.pagesIndex["p" + pageId];
		var paths = new Array();
		var ipath;
		//add paths in page index that are children of selected path
		for(i in index){
			ipath = index[i] + '.';
			if(ipath.indexOf(path) > -1) paths.push(index[i].slice(0));
		}
		pageId = null;  //release memory
	    index = null;
	    path = null;
	    ipath = null;
	    
		return paths;
	};
	
	/**
	 * Look in data repo and ensure this page item has at least one other
	 * copy currently present outside of the given node sub-cluster
	 */
	this.canDeleteResultNode = function(obj){
		//page must have at least two copies in the cluster
		var pageId = $(obj).attr('name');
		var node = this.root.getPathNode( this.displayedPagesNodePath ); 
		return node.canRemovePage(pageId);
	};
	
	/**
	 * Look in data repo and ensure all leaf nodes in this subtree
	 * have at least two copies currently present
	 */
	this.canDeleteNode = function(obj){
		var path = $(obj).parent().attr('name');  //obj = entity, parent li has path in name attribute
		var node = this.root.getPathNode(path);
		var pages = node.getChildPages();
		path = null;  //release memory
		for(p in pages)
			if(!node.canRemovePage(pages[p])){
				pages = null;
				node = null;
				return false;
			}

		pages = null;
		node = null;
		return true;
	};
	
	/**
	 * Should only be able to add a node if max depth constraint is not violated
	 */
	this.canAddNode = function(obj, type){
		var path = $(obj).parent().attr('name');  //obj = entity, parent li has path in name attribute
		var node = this.root.getPathNode(path);
		var a = type == 'bottom-node' ? 0 : 1;
		return node.getLevel() + a < this.maximumDepth;
	};
	
	/**
	 * Check if a node or page is currently being copied
	 */
	this.canPasteNodeOrPage = function(type){
		if(type == 'node') return this.activeNode != null;
		if(type == 'page') return this.activePage != null;
		return false;
	};
	
	/**
	 * Context menu Bind function
	 */
	this.contextMenu = function(event){
		//since right-click happened, drag is not valid
		Tree.dragEnd();
		
		var className = $(this).parent().attr('class');
		
		if(!Tree.editAllowed){ 
			//re-enable hidden menu items
			$('#ectMenuEditDisabled > span').show();
			$('#ectMenuEditDisabled').css('top',event.pageY).css('left',event.pageX).show();
			$('*').click(function() { $('#ectMenuEditDisabled').hide(); });
			return false; 
		}
		
		if(this.className == 'label'){
			$('.active', ulTree).removeClass('active');
			$(this).addClass('active');
		} else if(this.className == 'entry'){
			$('.active-page', resDiv).removeClass('active-page');
			$(this).parent().addClass('active-page');
		}
		
		//re-enable hidden menu items
		$('#ecTreeMenu > span').show();
		//hide inappropriate menu items
		if (className.indexOf('page') >= 0) {
			// leaf node result
			$('#ecTreeMenu .addNode, #ecTreeMenu .addBottomNode, #ecTreeMenu .copyNode, #ecTreeMenu .cutNode').hide();
			$('#ecTreeMenu .pasteNode, #ecTreeMenu .pasteResult, #ecTreeMenu .rename, #ecTreeMenu .deleteNode').hide();
			$('#ecTreeMenu .clearAllEdits, #ecTreeMenu .showWithoutEdits').hide();
			//if only one copy of this page result registered, do not allow cut or delete of this leaf
			if(!Tree.canDeleteResultNode(this))
				$('#ecTreeMenu .deleteResult').hide();
		} else if (className.indexOf('bottom-node') >= 0) {
			// bottom node
			$('#ecTreeMenu .addNode, #ecTreeMenu .addBottomNode, #ecTreeMenu .pasteNode').hide();
			$('#ecTreeMenu .cutResult, #ecTreeMenu .copyResult, #ecTreeMenu .deleteResult').hide();
			$('#ecTreeMenu .clearAllEdits, #ecTreeMenu .showWithoutEdits').hide();
			//do not allow cut or delete unless all leaf nodes from subtree have more than one copy registered
			if(!Tree.canDeleteNode(this))
				$('#ecTreeMenu .deleteNode').hide();
			//do not display pasteResult unless a document (page) has been copied
			if(!Tree.canPasteNodeOrPage('page'))
				$('#ecTreeMenu .pasteResult').hide();
			//if flat clustering, cannot add or copy a label node
			if(Tree.activeQueryParams['clusteringAlgo'] == 0)
				$('#ecTreeMenu .copyNode, #ecTreeMenu .cutNode').hide();
		} else if (className.indexOf('root') >= 0) {
			//root node
			$('#ecTreeMenu .copyResult, #ecTreeMenu .pasteResult, #ecTreeMenu .deleteResult').hide();
			$('#ecTreeMenu .cutResult, #ecTreeMenu .cutNode, #ecTreeMenu .rename, #ecTreeMenu .deleteNode').hide();
			//do not display pasteNode unless a node has been copied
			if(!Tree.canPasteNodeOrPage('node'))
				$('#ecTreeMenu .pasteNode').hide();
			//if flat clustering, cannot add or copy a label node, cannot expand or collapse
			if(Tree.activeQueryParams['clusteringAlgo'] == 0)
				$('#ecTreeMenu .addNode, #ecTreeMenu .copyNode').hide();
		} else {
			//inner node
			$('#ecTreeMenu .copyResult, #ecTreeMenu .pasteResult, #ecTreeMenu .deleteResult').hide();
			$('#ecTreeMenu .cutResult, #ecTreeMenu .deleteResult').hide();
			$('#ecTreeMenu .clearAllEdits, #ecTreeMenu .showWithoutEdits').hide();
			//do not allow cut or delete unless all leaf nodes from subtree have more than one copy registered
			if(!Tree.canDeleteNode(this))
				$('#ecTreeMenu .deleteNode').hide();
			//do not display pasteNode unless a node has been copied
			if(!Tree.canPasteNodeOrPage('node'))
				$('#ecTreeMenu .pasteNode').hide();
			//check max depth constraint before allowing to add a label
			if(!Tree.canAddNode(this, 'bottom-node'))
				$('#ecTreeMenu .addNode, #ecTreeMenu .addBottomNode').hide();
			else if(!Tree.canAddNode(this, 'node'))
				$('#ecTreeMenu .addNode').hide();
		}
				
		//show menu
		$('#ecTreeMenu').css('top',event.pageY).css('left',event.pageX).show();
		
		//register page event to hide menu and restore hidden sub-menus
		$('*').click(function() { 
			$('#ecTreeMenu').hide(); 
		});

		className = null;  //release memory
		
		return false;
	};
	
	/**
	 * Context menu Bind function for browse-only mode
	 */
	this.browseContextMenu = function(event){
		//since right-click happened, drag is not valid
		Tree.dragEnd();
		
		var className = $(this).parent().attr('class');
		
		if(this.className == 'label'){
			$('.active', ulTree).removeClass('active');
			$(this).addClass('active');
		} else if(this.className == 'entry'){
			$('.active-page', resDiv).removeClass('active-page');
			$(this).parent().addClass('active-page');
		}
		
		//re-enable hidden menu items
		$('#ectMenuBrowseTInit > span').show();
		
		//show menu
		$('#ectMenuBrowseTInit').css('top',event.pageY).css('left',event.pageX).show();
		
		//register page event to hide menu and restore hidden sub-menus
		$('*').click(function() { 
			$('#ectMenuBrowseTInit').hide(); 
		});
		
		className = null;  //release memory
		
		return false;
	};

	/**
	 * Properly handle both click and double-click.  Will be bound to click and dblclick events
	 */
	this.multiClick = function(obj) {
		//since click happened, drag is not valid
		Tree.dragEnd();
		
		var cls = obj.className;
		obj = $(obj);
		
		obj.each(function() {
		    var clicks = obj.data('clicks') || 0;
		    clicks++;
		    obj.data('clicks', clicks);
		    if (clicks == 1) {
                setTimeout(function() {
                	var clicks = obj.data('clicks') || 0;
                    obj.data('clicks', 0);
                    if (clicks == 1) {
                		$('.active', ulTree).removeClass('active'); //set old active back to label
                		if(cls == 'label') //enable new active
                			obj.addClass('active');
                		var path = obj.parent().attr('name');
                		if(path == 'ecTreeRoot') path = '0';
                		Tree.showNodePages(path);
                		path = null; //release memory
                    } else {
                    	//if event was double-clicked, toggle subtree
                    	Tree.subtreeToggle(obj.parent().get(0));
                    }
                    clicks = null;  //release memory
                    
                    return false;
                }, 200);
            }
		    clicks = null;  //release memory
		    return false;
		});
		path = null;  //release memory
		cls = null;
		
		return false;
	};
	
	
	//cluster actions
	/**
	 * Clear results and tree in preparation for another action
	 */
	this.clearCurrentSearch = function(){
		//clear timer messages and start execution timer
		this.clearTimerMessages();
		
		//clear stats and results footer
		this.showStats('&nbsp;');
		$('#' + Tree.resultsFooterDivElementId).html('');
		//clear label path changes
		this.clearLabelPathChanges();
		//clear selected and copy/cut buffers
		this.activeNode = null;
		this.activePage = null;
		this.activePageNodePath = '';
		this.activeRenameLabel = null;
		this.showResultsMessage('');
		//tagging items
		if(this.taggingEnabled){
			this.tagItems = {};
			this.tagSelectPaths = [];
			this.tagExecutionInfo = {};
		}
		//hide existing cluster
		$('#' + this.treeDivElementId).hide();
	};
	
	/**
	 * Execute a search request and display search results and cluster tree
	 */
	this.executeSearch = function (startAt, query, engine, itemsPerPage, lastResult, clusteringAlgo){
		
		this.clearCurrentSearch();
		
		if(typeof query != "string") query = '';
		
		var includeEdits = 0; //only show TInit if params not received		
		if(typeof startAt != "undefined")
			includeEdits = 1; //default to retrieve edited paths
		
		//build web service url.  adding .json extension on last term to signal we want json response
		var url = '';
		if(includeEdits == 0){
			//retrieve parameters from data store
			startAt = this.activeQueryParams['startAt'];
			query = this.activeQueryParams['query'];
			engine = this.activeQueryParams['engine'];
			itemsPerPage = this.activeQueryParams['itemsPerPage'];
			lastResult = this.activeQueryParams['lastResult'];
			clusteringAlgo = this.activeQueryParams['clusteringAlgo'];
			
			url = this.activeQueryParams['url'];
		} else {
			var l = window.location;
			url = l.protocol + '//' + l.hostname;
			if(l.port != 80) url += ':' + l.port;
			url += '/' + l.pathname.substring(1, l.pathname.lastIndexOf('/'));
			l = null;
			
			//store query params that were executed
			this.activeQueryParams['url'] = url;
			this.activeQueryParams['clusteringAlgo'] = clusteringAlgo;
			this.activeQueryParams['engine'] = engine;
			this.activeQueryParams['query'] = query;
			this.activeQueryParams['itemsPerPage'] = itemsPerPage;
			this.activeQueryParams['startAt'] = startAt;
		}
		this.activeQueryParams['includeEdits'] = includeEdits;
		url += '/rest/clusterJson/' +
			clusteringAlgo + '/abs/' + engine + '/' + escape(query) + 
			'/' + itemsPerPage + '/' + startAt + '/' + includeEdits + '.json';
		
		query = $.trim( query );
		if(query == ''){
			this.showResultsMessage('Please enter search criteria.', 'error');
			return;
		}
		
		this.showResultsMessage('Please wait... <img src="includes/images/loading.gif">');
		
		//validate parameters for google and yahoo search sources - this should be moved to be an
		//exception thrown by AbstractSearch at some point in time
		if(engine == 'google' && (lastResult > 61 )){
			var message = '<span class="error">Google API does not allow retrieving more than 60 results.</span>';
			if(parseInt(startAt) - parseInt(itemsPerPage) >= 0)
				message += '<br/><br/><a href="javascript:' + this.searchFunctionName + '(' + (parseInt(startAt) - 
					parseInt(itemsPerPage)) + ')">Back</a>';
			this.showResultsMessage(message);
			message = null;  //release memory
			return;
		}
		
		if(engine == 'yahoo' && (lastResult > 1001 )){
			var message = '<span class="error">Yahoo! API does not allow retrieving more than 1000 results.</span>';
			if(parseInt(startAt) - parseInt(itemsPerPage) >= 0)
				message += '<br/><br/><a href="javascript:' + this.searchFunctionName + '(' + (parseInt(startAt) - 
					parseInt(itemsPerPage)) + ')">Back</a>';
			this.showResultsMessage(message);
			message = null;  //release memory
			return;
		}
		
		//signal re-clustering
		setDivMessage(this.treeMessageElementId, 'Please wait. Reclustering...', 1000);
		
		
		$.ajax({
	        url: url,
	        type: 'POST',
	        contentType: 'application/json',
			async: true,
			dataType: 'json',
			success: function(data){
			    if(typeof data == "object" && data != null && typeof data.success != "undefined" && data.success == true){
			    	
			    	Tree.presentationTimer.startTimer();
			    	
			    	var totalResults = data.results.totalResults;
		    		var returnedCount = data.results.returnedCount;
		    		var fistPosition = data.results.firstPosition;
		    		var results = data.results.results;
		    		var errors = data.results.errors;
		    		var clusterKey = data.results.clusterKey;
		    		var length = results != null ? results.length : 0;
		    		var timers = data.timers;
		    		
		    		if(Tree.activeQueryParams['includeEdits'] == 1) {
			    		Tree.activeQueryParams['queryId'] = data.query_id;
		    		}
		    		
		    		if(typeof data.error != "undefined"){
			        	//general exception
			        	Tree.showResultsMessage(data.error, 'error');
			        } else if(length == 0){
			        	Tree.showResultsMessage('Your query did not produce any results.', 'error');
			        } else {
			        
			        	Tree.showStats('Top ' + length + ' of ' + totalResults + ' results');
			        	
			        	Tree.results = data.results;
				    	
			        	//build cluster
				        Tree.buildClusterTree(data, includeEdits);
				        
			        	//clear wait message
			        	resDiv.html('');
			        	
			    		Tree.showNodePages();
				        
			        }
			        
			        if(errors != null && errors.length > 0){
			        	resDiv.append(
			        		'<span class="error"><br><br>Search produced one or more errors:<br><br>' +
			        		errors.join('<br>') +
			        		'</span>'
			        	);
			        }
			        
			        Tree.showTimerMessages(timers);
			        
			        totalResults = null;  //release memory
			        returnedCount = null;
		    		fistPosition = null;
		    		results = null;
		    		errors = null;
		    		clusterKey = null;
		    		length = null;
		    		timers = null;
			    } else {
			    	var message = 'Your query did not produce any results.';
			    	if(typeof data.error != "undefined") message = data.error;
			    	Tree.showResultsMessage(message, 'error');
			    	message = null;
			    }
			},
			error: function(oXHR, status){
				Tree.showResultsMessage('Server connection error.  Please try again.', 'error');
			}
	    });
		url = null;
		
	};
	
	/**
	 * Send paths to be added/subtracted to the database
	 */
	this.sendPathUpdates = function(){
		
		//build web service url.  Method returns JSON data by default.
		var url = this.activeQueryParams['url'] + '/rest/clusterUpdate/' +
			this.activeQueryParams['queryId'] + '/' +  
			this.activeQueryParams['clusteringAlgo'];

		var paths = this.jsonLabelPathSet();
		
		this.showTreeMessage('Please wait... <img src="includes/images/loading.gif">');
		
		$.ajax({
	        url: url,
	        type: 'POST',
	        contentType: 'multipart/form-data',
			async: false,
			data: paths,
			processData: false,
			dataType: 'json',
			success: function(data){
			    if(typeof data == "object" && data != null){
			    	if(typeof data.error != "undefined")
			    		throw new Tree.ecTreeOpException(data.error, 'sendPathUpdates');
			    	if(typeof data.success == "undefined" || data.success != true)
			    		throw new Tree.ecTreeOpException('Path update operation failed.  Please re-execute your search and try again.', 'sendPathUpdates');
			    } else {
			    	throw new Tree.ecTreeOpException('Unknown problem updating the paths in your cluster edit.  Please re-execute your search and try again.', 'sendPathUpdates');
			    }
			    data = null;
			},
			error: function(oXHR, status){
				throw new Tree.ecTreeOpException('Server connection error.  Please try again.', 'sendPathUpdates');
			}
	    });
		
		url = null;
		paths = null;
		
	};
	
	/**
	 * Enable drag-and-drop for a result node
	 */
	this.resultDragStart = function(){
		Tree.mousePressed = false;
		Tree.dragCloneItem = null;
		Tree.dragCounter = 0;
		if(Tree.dragAllowed && Tree.editAllowed)
		{
			Tree.mousePressed = true;
			Tree.dragCloneItem = $(this).parent();
			var liElem = $(this).parent();
			
			$('#ectDragContainer >ul').html(''); //clear previous dragged elem just in case
			$('#ectDragContainer').hide().css({opacity:'0.8'});
			var dragContent = Tree.dragCloneItem.clone();
			$('>div', dragContent).remove();
			$('#ectDragContainer >ul').html(dragContent); 
			$(document).bind("mousemove", {liElem: liElem}, Tree.dragStart)
				.bind("mouseup", Tree.dragEnd);
			liElem = null;
		}
		
		return false;
	};
	
	/**
	 * Tree node drag start
	 */
	this.nodeDragStart = function(){
		Tree.mousePressed = false;
		Tree.dragCloneItem = null;
		Tree.dragCounter = 0;
		if(Tree.dragAllowed && Tree.editAllowed)
		{
			Tree.mousePressed = true;
			Tree.dragCloneItem = $(this).parent();
			var liElem = $(this).parent();
			
			$('#ectDragContainer >ul').html(''); //clear previous dragged elem just in case
			
			$('#ectDragContainer').hide().css({opacity:'0.8'});
			$('#ectDragContainer >ul').html(Tree.dragCloneItem.clone()); 
			$(document).bind("mousemove", {liElem: liElem}, Tree.dragStart)
				.bind("mouseup", Tree.dragEnd);
			liElem = null;
		}
		
		return false;
	};
	
	/**
	 * Action to be performed when dragging is complete and has ended on a tree node
	 */
	this.executeDragEnd = function(event){
		if(Tree.mousePressed && Tree.dragNodeSource && Tree.editAllowed && 
				Tree.dragCounter > 2) {
			var moveItem = $(Tree.dragCloneItem).attr('name');
			var moveItemClass = $(Tree.dragCloneItem).attr('class');
			var innerNode = moveItemClass.indexOf('cluster') > -1;
			var bottomNode = moveItemClass.indexOf('bottom') > -1;
			var resultNode = moveItemClass.indexOf('page') > -1;
			var targetItem = $(this).parent().attr('name');
			var targetItemClass = $(this).parent().attr('class');
			//check if copy/move allowed
			if ((targetItemClass.indexOf('cluster') > -1 || targetItemClass.indexOf('root') > -1)
					&& (innerNode || bottomNode)){
				if(!event.ctrlKey){ //if CTRL is not pressed, move, else copy
					Tree.cutClusterNode(Tree.dragCloneItem);
				} else {
					Tree.copyClusterNode(Tree.dragCloneItem);
				}
				Tree.pasteClusterNode($(this).parent());
			} else if (targetItemClass.indexOf('bottom') > -1 && resultNode) {
				if(!event.ctrlKey){
					Tree.cutResultPage($('>div.entry', Tree.dragCloneItem));
				} else {
					Tree.copyResultPage($('>div.entry', Tree.dragCloneItem));
				}
				Tree.pasteResultPage($(this).parent());
			}
			moveItem = null;
			moveItemClass = null;
			innerNode = null;
			bottomNode = null;
			resultNode = null;
			targetItem = null;
			targetItemClass = null;
		}
		Tree.dragEnd();
	};
	
	/**
	 * Function called when dragging starts in document
	 */
	this.dragStart = function(event){
		if(!Tree.editAllowed) return; 
		var liElem = $(event.data.liElem);
		var liElemClass = liElem.attr('class');
		var innerNode = liElemClass.indexOf('cluster') > -1;
		var bottomNode = liElemClass.indexOf('bottom') > -1;
		var resultNode = liElemClass.indexOf('page') > -1;
		if(Tree.mousePressed && Tree.dragAllowed) {
			Tree.dragCounter += 1;
			
			if(Tree.dragCounter == 2){
				if(Tree.dragCloneItem.attr('class').indexOf('-last') > -1)
					//if node to be moved is last, make its predecessor not last
					Tree.makeNodeLast(Tree.dragCloneItem.prev('li'));
			}
			
			//if context menu was showing, hide it
			$('#ecTreeMenu').hide();
			
			if(Tree.dragCounter > 2) {
				if(innerNode)
					$('>ul', Tree.dragCloneItem).hide();
				if($('#ectDragContainer:not(:visible)')) {
					$('#ectDragContainer').show();
					Tree.dragNodeSource = liElem;				
				}
				$('#ectDragContainer').css({position:'absolute', "left" : (event.pageX + 5), "top": (event.pageY + 15) });
				if(liElem.is(':visible')) liElem.hide();
				
				if(event.target.tagName.toLowerCase() == 'span' && event.target.className.indexOf('label') > -1 ) {
					var parent = event.target.parentNode;
					var offs = $(parent).offset();
					var screenScroll = {x : (offs.left - 3),y : event.pageY - offs.top};
					screenScroll.x += 19;
					screenScroll.y = event.pageY - screenScroll.y + 5;
	
					if(parent.className.indexOf('bottom') > -1) {
						if(resultNode)
							$("#ectTreePlus").css({"left": screenScroll.x, "top": screenScroll.y}).show();
					} else if(innerNode || bottomNode) {
						$("#ectTreePlus").css({"left": screenScroll.x, "top": screenScroll.y}).show();
					}
					parent = null;
					offs = null;
					screenScroll = null;
				} else {
					$("#ectTreePlus").hide();
				}
			}
			var liElem = null;
			var liElemClass = null;
			var innerNode = null;
			var bottomNode = null;
			var resultNode = null;
			return false;
		}
		var liElem = null;
		var liElemClass = null;
		var innerNode = null;
		var bottomNode = null;
		var resultNode = null;
		return true;
	};
	
	/**
	 * Function called on end of drag event to clear dragging elements
	 */
	this.dragEnd = function(){
		//clear dragging event
		$(document).unbind('mousemove', Tree.dragStart).unbind('mouseup').unbind('mousedown');
		$('#ectDragContainer, #ectTreePlus').hide();
		$('#ectDragContainer >ul').html('');
		if(Tree.dragNodeSource && Tree.dragCounter > 0){
			//if we transformed prev li to -last, transform back
			if(Tree.dragNodeSource.prev('li').length > 0 && Tree.dragNodeSource.prev('li').attr('class').indexOf('-last') > -1)
				Tree.makeNodeNotLast(Tree.dragNodeSource.prev('li'));
			$(Tree.dragNodeSource).show();
			if(Tree.dragNodeSource.attr('class').indexOf('-open') > -1)
				Tree.nodeToggle(Tree.dragNodeSource);
		}
		Tree.dragNodeDestination = Tree.dragNodeSource = Tree.mousePressed = false;
		Tree.dragCloneItem = null;
		Tree.dragCounter = 0;
	};
	
	
	/**
	 * Display results for a selected node
	 */
	this.showNodePages = function(path){
		
		if(typeof path == "undefined") 
			path = '0';
		else {
			//add path to list of visitted paths for tagged items
			//we don't count the initial display of results in the page(path == undefined)
			//if the user selects a label, then we add it
			if(this.taggingEnabled)
				this.tagSelectPaths[this.tagSelectPaths.length] = path;
		}
			
		this.displayedPagesNodePath = path;
		
		
		
		var results;
		var node;
		var labels;
		if(path == '0'){  //this is the root
			results = this.results.results;
			node = this.root;
			labels = [this.activeQueryParams['query']]; //words to be highlighted - if All is selected, highlight query words
		} else {
			node = this.root.getPathNode(path);
			var pages = node.getChildPages();
			pages = this.getArraySet(pages, true); //make set of pages
			results = new Array();
			for(p in pages)
				results.push(this.results.results[pages[p]]);
			pages = null;  //release memory
			labels = node.getLabelPath(path); //words to be highlighted - labels of current and parent nodes
			labels[0] = this.activeQueryParams['query']; //highlight query terms as well
		}
		var addedPages = node.getAddedPages();
		addedPages = this.getArraySet(addedPages, true);
		
		//clear results div
    	resDiv.html('');
    	var j = 0;
    	var addClass = '';
    	var activePageId = this.activePage == null ? -1 : this.activePage.attr('name');
    	if(results.length == 0){
    		resDiv.html( this.emptyClusterWarning );
    	} else    	
	    	for(var i = 0; i < results.length; i++ ){
	        	var result = results[i];
	        	
	        	//check if page is an added page
	        	while(j < addedPages.length && addedPages[j] < result.index) j++;
	        	
	    		if(this.activePageNodePath == path && activePageId == result.index)
	    			addClass = this.activePageOp == 'copy' ? ' copied-page' : ' cut-page';
	    		else addClass = '';
	    		
	        	if(result.snippet == null || result.snippet == 'null' || result.snippet == ''){
	        		result.snippet = 'No summary available.';
	        	} else {
	        		if(typeof this.highlightFunction == 'function')
	        			result.snippet = this.highlightFunction(labels, result.snippet);
	        	}
	        	
	        	if(jQuery.trim(result.title) == ''){
	        		result.title = 'Title not availabe';
	        	} else {
	        		if(typeof this.highlightFunction == 'function')
	        			result.title = this.highlightFunction(labels, result.title);
	        	}
	        	
	        	//build a result element
	        	var elem = '<div class="page' + addClass + '">' + result.id + 
	        		'. <a href="' + result.clickUrl + '" class="title" target="_blank" >' + 
	        			result.title + '</a>';
	        		if(addedPages[j] == result.index) 
	        			elem += '  <span class="addedPath" title="Previously edited result node">*</span>';
	        		
	        		//if ongoing test requiring tagging
	        		if(this.taggingEnabled == true)
	        			elem += this.getTag(path, result.index);
	        		
	        		elem += '<div class="entry" name="' + result.index + 
	        		'"><span class="snippet">' + result.snippet +
	    			'</span><br/><span class="url">' + result.url + '</span>';
	        	if(result.cacheURL != null) elem += '- ' +
	    			'<a href="' + result.cacheUrl + '" class="cached">Cached Result</a>';
	        	elem += '</div></div>';
	        	
	        	resDiv.append(elem);
	        	
	        	result = null;  //release memory
	        	elem = null;
	        }
    	results = null;  //release memory
    	labels = null;
    	
    	//attach contextmenu and other events to entry elements
    	$('.entry', resDiv)
        .bind('selectstart', function() {  //disable text selection for labels in the tree
 			return false;
 		}).dblclick(function(){
 			return false;
 		}).click(function(){
 			$('.active-page', resDiv).removeClass('active-page'); //set old active back to label
     		if($(this).attr('class') == 'entry') //enable new active
     			$(this).parent().addClass('active-page');  
     		return false;
 		});
    	
    	if(this.activeQueryParams['includeEdits'] == 1){ //if tree is nor TInit in browse only mode
    		$('.entry', resDiv).bind(                            //enable context menu
    	 		"contextmenu", Tree.contextMenu
     		).mousedown( //enable drag-and-drop
     			Tree.resultDragStart
     		).mouseup(
     			Tree.executeDragEnd
     		);
    	} else {
    		$('.entry', resDiv).bind(
				"contextmenu", Tree.browseContextMenu
			).mousedown(function(event){
	 			return false;
	 		}).mouseup(function(){
	 			return false;
	 		});
    		setDivMessage(Tree.treeMessageElementId, 'Click <a id="ectReEnableEditing" href="#">here</a> to re-enable editing.');
    		$('#ectReEnableEditing').click(function(){ Tree.reEnableEditing(); });
    	}
    	
 		//tag node in tree as being selected
 		$('.selected', ulTree).removeClass('selected'); //set old active back to label
 		$("li[name='" + path + "']", ulTree).addClass('selected');
 		
        path = null;
        node = null;
        addedPages = null;
        j = null;
	};
	
	/** TAGGING methods **/
	
	
	/**
	 * The tag object allows tracking results the user has tagged
	 * @param path String node id path for the selected node result was tagged in
	 * @param resultId the id of the result that was tagged
	 */
	this.tag = function(path, resultId){
		
		this.path = path;      
		this.resultId = resultId;  //index in this.results array for given result
		//array of indexes of child nodes along the path.
		//the last elem of the array is the index of the result within the
		//base cluster results array.  indexes are 1 based
		this.indexPath = Tree.root.getChildIndexPath(path, resultId);
				
		//compute user effort for this tag
		this.resultEffort = 0;
		
		//cost is index within cluster (+1) unless there is another tagged item
		//with a higher index, in which case it is 0.
		this.computeResultEffort = function(){
			var cost = this.indexPath[this.indexPath.length - 1];
			for(var i in Tree.tagItems){
				if(this.path == Tree.tagItems[i].path){
					var ip = Tree.tagItems[i].indexPath;
					var ri = ip[ip.length - 1];
					if(ri > cost){
						cost = 0;
						ri = null;
						ip = null;
						break;
					}
				}
			}
			this.resultEffort = cost;
			return cost;
		}
		
		this.toJSON = function(){
			return '{path: ' + this.path + 
				', indexPath: ' + this.indexPath + 
				', resultId: ' + this.resultId + 
				', resultEffort: ' + this.resultEffort + '}';
		}
		
	};
	
	this.setTagCount = function(cnt){
		this.tagCount = cnt;
	};
	
	/**
	 * If tagging is enabled, get display info for tag for given result
	 */
	this.getTag = function(path, resultIndex){
		if(!this.taggingEnabled) return '';
		var key = resultIndex + '';
		var tagged = false;
		if(key in this.tagItems){
			tagged = true;
		}
		key = tagged ? 'tagged' : 'not_tagged';
		var img = ' &nbsp; <img src="includes/images/' + key + 
			'.png" onClick="ect.setTag(this, \'' + path + 
			'\', \'' + resultIndex + '\')" class="hovimg">';
		key = null;
		tagged = null;
		return img;
	};
	
	/**
	 * Check how many result tags have been set
	 */
	this.countTags = function(){
		var count = 0;
		for (key in this.tagItems) 
			if (this.tagItems.hasOwnProperty(key)) count++;
		return count;
	};
	
	/**
	 * Clear tags that have been already set
	 */
	this.clearTags = function(){
		this.tagItems = {};
		this.tagCount = 0;
	};
	
	/**
	 * Get a reference to the tag items
	 */
	this.getTagItems = function(){
		return this.tagItems;
	};
	
	/**
	 * Get a reference to the list of all selected paths
	 */
	this.getTagSelectPaths = function(){
		return this.tagSelectPaths;
	};
	
	/**
	 * Get a reference to the tagExecutionInfo object
	 */
	this.getTagExecutionInfo = function(){
		return this.tagExecutionInfo;
	};
	
	/**
	 * Get a set of selected paths that were visited 
	 * but nothing was tagged in them
	 * We assume that even if user re-visits the path
	 * they don't rescan the tree as they are already 
	 * familiar with the scanned labels. This is
	 * acceptable for relatively small trees.
	 */
	this.getTagVisitOnlySelectPaths = function(){
		var p = this.tagSelectPaths.slice(0);
		//remove any paths that have tagged items
		for(var i = 0; i < p.length; i++){
			var found = false;
			for(var j in this.tagItems){
				if(this.tagItems[j].path == p[i]){
					found = true;
					break;
				}
			}
			if(found)
				p.splice(i--, 1);
		}
		return this.getArraySet(p);
	};
	
	/**
	 * Compute user effort for the paths that were visited but nothing
	 * was tagged in them
	 * We assume that some of the select paths were selected by mistake, 
	 * or the user only looked at one or two results before selecting another
	 * path. As such we count only a percentage of the user cost for
	 * scanning all results in those paths (ex: 0.5).
	 */
	this.scoreTagVisitOnlySelectPaths = function(){
		var visitOnlyPaths = this.getTagVisitOnlySelectPaths();
		var cost = 0;
		for(var i in visitOnlyPaths){
			var path = visitOnlyPaths[i];
			var node = this.root.getPathNode(path);
			cost += node.getUniquePageCount();
			path = null;
			node = null;
			pages = null;
		}
		
		cost *= this.tagVisitOnlyPathsRate;
		
		visitOnlyPaths = null;
		return cost;
		
	};
	
	/**
	 * Set a tag for a given item
	 */
	this.setTag = function(obj, path, resultId){
		var cnt = this.countTags();
		var s = obj.src;
		var tag = s.indexOf('not_tagged') > -1;
		var key = resultId + '';
		if(tag){
			if(this.tagCount > 0 && cnt >= this.tagCount){
				alert('All necessary tags have been set.  Please unset some tags before setting the current tag.');
				key = null;
				s = null;
				tag = null;
				cnt = null;
				return;
			}
			this.tagItems[key] = new this.tag(path, resultId);
			s = s.replace('not_tagged', 'tagged');
			cnt += 1;
		} else {
			delete this.tagItems[key];
			s = s.replace('tagged', 'not_tagged');
			cnt -= 1;
		}
		if(this.tagCount > 0){
			if(this.tagCount - cnt > 0 || !this.editAllowed) {
				$('#' + this.taggingElementId).html((this.tagCount - cnt) + ' remaining tags.');
				$('#' + this.taggingElementId).attr('class', 'success');
			} else {
				$('#' + this.taggingElementId).html('0 remaining tags. Please proceed with cluster editing.');
				$('#' + this.taggingElementId).attr('class', 'error');
			}
		}
		this.setTagExecutionInfo();
		obj.src = s;
		key = null;
		s = null;
		tag = null;
		cnt = null;
	};
	
	/**
	 * Set tag execution info given current data at set (mod 5) intervals
	 * Delete stale data if tags were removed past recording threshold
	 */
	this.setTagExecutionInfo = function(mustSet){
		if(typeof mustSet == "undefined") mustSet = false;
		var cnt = this.countTags();
		if(!mustSet && (cnt == 0 || cnt % 5 != 0)) return;
		
		var tags = {};
		var userEffort = 0;
		var baseEffort = 0;
		var baseRelevantEffort = 0;
		tags['tags'] = this.getTagItems();
		tags['userSelectedPathsList'] = this.getTagSelectPaths();
		tags['pathScanningUserEffort'] = this.getPathScanningUserEffort();
		tags['visitOnlyPathSet'] = this.getTagVisitOnlySelectPaths();
		tags['visitOnlyPathsScore'] = this.scoreTagVisitOnlySelectPaths();
		
		userEffort = this.getUserEffort();
		baseEffort = this.getBaseEffort();
		baseRelevantEffort = this.getBaseRelevantEffort();
	
		var info = {userEffort: userEffort, 
			baseEffort: baseEffort,
			baseRelevantEffort: baseRelevantEffort,
			tags: tags };
		this.tagExecutionInfo[cnt + ""] = this.clone( info );
		info = null;
		tags = null;
		userEffort = null;
		baseEffort = null;
		baseRelevantEffort = null;
		cnt = null;
	};
	
	
	/**
	 * When testing, we compute the base relevant effort for
	 * this.tagCount items by itterating through the first this.tagCount
	 * relevant items in the results set, adding 1 for each visitted
	 * item until all items are found
	 */
	this.getBaseRelevantEffort = function(){
		if(this.tagCount == 0) return 0;
		var res = this.results.results;
		var c = 0;
		var found = 0;
		for(var i = 0; i < res.length; i++){
			if(res[i].relevant > 0){ 
				found += 1;
				c = res[i].index + 1;
			}
			if(found == this.tagCount)
				break;
		}
		res = null;
		return c;
	};
	
	/**
	 * Get user effort for scanning paths
	 * We add the cost of scanning labels for all the paths that were visitted
	 * for multiple paths in the same level with the same parents we only count
	 * the path with the higher index in the level
	 */
	this.getPathScanningUserEffort = function(){
		
		//break paths into sets of same parent paths
		var paths = this.getArraySet(this.tagSelectPaths.slice(0));
		var parPaths = {};
		for(var i in paths){
			var p = paths[i];
			var indexPath = this.root.getChildIndexPath(p); //array will not contain result index within cluster as not passing result id
			var key = indexPath.length > 1 ? indexPath.slice(0, indexPath.length - 1).join('.') : 'r';
			if(key in parPaths){
				var it = parPaths[key];
				if(it[it.length - 1] < indexPath[indexPath.length - 1])
					parPaths[key] = indexPath.slice(0);
				it = null;
			} else 
				parPaths[key] = indexPath.slice(0);
			p = null;
			indexPath = null;
			key = null;
		}
		var ue = 0;
		for(var key in parPaths) {
			//for each path with common parents and highest index, we add user effort
			var it = parPaths[key];
			for(var i = 0; i < it.length; i++){
				//the cost for scanning each label is 0.25 points
				ue += 0.25 * it[i];
			}
			it = null;
		}
		return ue;
	};
	
	/**
	 * We compute the user effort by adding individual effort for 
	 * the items tagged + effort for paths that were selected but nothing
	 * was tagged in them, assuming the user does some scanning of 
	 * results in those paths as well
	 */
	this.getUserEffort = function(){
		if(this.tagCount == 0) return 0;
		var ue = 0;
		for(var i in this.tagItems){
			ue += this.tagItems[i].computeResultEffort();
		}
		ue += this.scoreTagVisitOnlySelectPaths();
		ue += this.getPathScanningUserEffort();
		return ue;
	};
	
	/**
	 * Same as user effort except we only looking at the initial
	 * ranked list
	 */
	this.getBaseEffort = function(){
		if(this.tagCount == 0) return 0;
		//first identify result indexes tagged
		var resIds = [];
		for(var i in this.tagItems)
			resIds[resIds.length] = parseInt(this.tagItems[i].resultId);
		
		//order the list, as user would find them during one scan
		resIds.sort(function(a,b){return b - a});
		//the total effort is 1 higher than the highest index (1-based)
		var be = resIds[0] + 1; //list was ordered in reverse order
		//base effort is total effort
		return be;
	};
	
	/** NODE functions */
	
	/**
	 * Activate functionality of tree nodes
	 */
	this.addNodeEvents = function(obj, copy){
		
		if(typeof copy == "undefined") copy = false;
		
		$('span', obj)                     //apply following events to span elements within the tree
		.bind('selectstart', function() {  //disable text selection for labels in the tree
			return false;
		}).dblclick(function(){
			return Tree.multiClick(this);
		}).click(function(){
			return Tree.multiClick(this);
		}).bind(                            //enable context menu
			"contextmenu", Tree.contextMenu
		).mousedown( 
			Tree.nodeDragStart 
		).mouseup(
			Tree.executeDragEnd
		);
		
		//if this a copy elem, we must add triggers to current elem as well
		if(copy){
			var className = obj.get(0).className;
			var childNode = $('>ul', obj);
			//if it has child ul elements
			if(childNode.size() > 0 ){
				//and are supposed to be closed
				if( className && className.indexOf('closed') > -1 ) 
					childNode.hide();     //hide its children
				//set up toggle of li elems with children ul elems
				Tree.enableToggle(obj);
			}
			className = null;  //release memory
			childNode = null;
		}

		//for each li element within subtree
		$('li', obj).each(function(i){
			var className = this.className;
			var childNode = $('>ul',this);
			//if it has child ul elements
			if(childNode.size() > 0 ){
				//and are supposed to be closed
				if( className && className.indexOf('closed') > -1 ) 
					childNode.hide();     //hide its children
				//set up toggle of li elems with children ul elems
				Tree.enableToggle(this);
			}
			className = null;  //release memory
			childNode = null;
		});
	};
	
	/**
	 * Activate functionality of T-init browse only tree
	 */
	this.addTInitNodeEvents = function(obj, copy){
		
		if(typeof copy == "undefined") copy = false;
		
		$('span', obj)                     //apply following events to span elements within the tree
		.bind('selectstart', function() {  //disable text selection for labels in the tree
			return false;
		}).dblclick(function(){
			return Tree.multiClick(this);
		}).click(function(){
			return Tree.multiClick(this);
		}).bind(                            //enable context menu
			"contextmenu", Tree.browseContextMenu
		).mousedown(function(event){
			return false;
		}).mouseup(function(){
			return false;
		});
		
		//if this a copy elem, we must add triggers to current elem as well
		if(copy){
			var className = obj.get(0).className;
			var childNode = $('>ul', obj);
			//if it has child ul elements
			if(childNode.size() > 0 ){
				//and are supposed to be closed
				if( className && className.indexOf('closed') > -1 ) 
					childNode.hide();     //hide its children
				//set up toggle of li elems with children ul elems
				Tree.enableToggle(obj);
			}
			className = null;  //release memory
			childNode = null;
		}

		//for each li element within subtree
		$('li', obj).each(function(i){
			var className = this.className;
			var childNode = $('>ul',this);
			//if it has child ul elements
			if(childNode.size() > 0 ){
				//and are supposed to be closed
				if( className && className.indexOf('closed') > -1 ) 
					childNode.hide();     //hide its children
				//set up toggle of li elems with children ul elems
				Tree.enableToggle(this);
			}
			className = null;  //release memory
			childNode = null;
		});
	};
	
	/**
	 * Create internal representation of the tree from the JSON data received from the server
	 */
	this.buildClusterTree = function(data, includeEdits){
		this.customResultLabels = data.cluster.customResultLabels;
		//reset pages index
		this.pagesIndex = {};
		//create tree
		this.root = new Tree.node(null, data.cluster);
		
		//place tree in document
		$('#' + this.treeDivElementId).html( this.root.toHTML()).show();
		ulTree = $('#ecTreeUl').get(0);
		liRoot = $('#ecTreeRoot').get(0);
		
		if(includeEdits == 1)
			this.addNodeEvents(liRoot);
		else 
			this.addTInitNodeEvents(liRoot);
		
		if(this.editAllowed)
			this.showTreeMessage('Right-click on nodes to access tree operations.');
		
	};
	
	/**
	 * Clear all edits for given query
	 */
	this.clearAllEdits = function(){
		//request db clear of all path edits for given query and re-do search to rebuild tree
		
		//build web service url.  method returns JSON data by default
		var url = this.activeQueryParams['url'] + '/rest/deleteClusterEdits/' +
			this.activeQueryParams['queryId'] + '/' + this.activeQueryParams['clusteringAlgo'];

		this.showTreeMessage('Please wait... <img src="includes/images/loading.gif">');
		
		try {
			$.ajax({
		        url: url,
		        type: 'POST',
				async: false,
				dataType: 'json',
				success: function(data){
				    if(typeof data == "object" && data != null){
				    	if(typeof data.error != "undefined")
				    		throw new Tree.ecTreeOpException(data.error, 'clearAllEdits');
				    	if(typeof data.success == "undefined" || data.success != true)
				    		throw new Tree.ecTreeOpException('Clear edits operation failed.  Please re-execute your search and try again.', 'sendPathUpdates');
				    } else {
				    	throw new Tree.ecTreeOpException('Unknown problem clearing edits in your cluster edit.  Please re-execute your search and try again.', 'sendPathUpdates');
				    }
				    data = null;
				},
				error: function(oXHR, status){
					throw new Tree.ecTreeOpException('Server connection error.  Please try again.', 'sendPathUpdates');
				}
		    });
		} catch (e){
			var message = e instanceof this.ecTreeOpException ? e.getMessage() : e;
			this.showTreeMessage(message, 'error');
			message = null;  //release memory
			return;
		}
		
		url = null;
		
		//once clear is complete, re-build tree
		this.executeSearch(
			this.activeQueryParams['startAt'], 
			this.activeQueryParams['query'], 
			this.activeQueryParams['engine'], 
			this.activeQueryParams['itemsPerPage'], 
			this.activeQueryParams['lastResult'], 
			this.activeQueryParams['clusteringAlgo']
		);
	};
	
	/**
	 * Re-enable editing on the tree
	 */
	this.reEnableEditing = function() {
		this.executeSearch(
			this.activeQueryParams['startAt'], 
			this.activeQueryParams['query'], 
			this.activeQueryParams['engine'], 
			this.activeQueryParams['itemsPerPage'], 
			this.activeQueryParams['lastResult'], 
			this.activeQueryParams['clusteringAlgo']
		);
	};
	
	/**
	 * Get last node within children
	 * Should be provided an ul element
	 */
	this.getLastChild = function(node){
		var sibs = node.children();
		if(sibs.size() == 0) return false;
		var last = sibs.get( sibs.size() - 1);
		sibs = null;  //release memory
		
		return $(last);
	};
	
	/**
	 * Check if node is last within siblings
	 */
	this.isLastNode = function(node){
		if(node.attr('name') == '0') return true;
		var last = this.getLastChild(node.parent());
		return last.attr('name') == node.attr('name');
	};
	
	/**
	 * Convert given node from an internal node to a bottom node
	 */
	this.convertToBottomNode = function(node){
		$('>ul', node).remove();
		$('img', node).remove();
		node.removeClass('cluster-open').removeClass('cluster-closed').addClass('bottom-node');
	};
	
	/**
	 * Convert to internal node from bottom-node
	 */
	this.convertToInnerNode = function(node){
		node.removeClass('bottom-node').addClass('cluster-open');
		this.setTrigger(node);
	};
	
	/**
	 * Convert a last node to a not-last node in the display tree
	 */
	this.makeNodeNotLast = function(node){
		if(typeof node != "object" || node.length == 0) return;
		var cls = node.attr('class');
		//class attr does not hold dynamically added DOM classes
		var active = node.hasClass('active');
		var selected = node.hasClass('selected');
		var copied = node.hasClass('copied');
		var cut = node.hasClass('cut');
		
		node.attr('class', cls.replace('-last', ''));

		if(active) node.addClass('active');
		if(selected) node.addClass('selected');
		if(copied) node.addClass('copied');
		if(cut) node.addClass('cut');
		
		cls = null;
	};
	
	/**
	 * Convert a non-last node to a last node in the display tree
	 */
	this.makeNodeLast = function(node){
		if(typeof node != "object" || node.length == 0) return;
		var classList = node.attr('class').split(/\s+/); 
		//class attr does not hold dynamically added DOM classes
		var active = node.hasClass('active');
		var selected = node.hasClass('selected');
		var copied = node.hasClass('copied');
		var cut = node.hasClass('cut');
		
		$.each( classList, function(index, item){ 
			if(classList[index].indexOf('-last') == -1 && 
			   (classList[index].indexOf('cluster') > -1 || 
			    classList[index].indexOf('bottom') > -1) )
			classList[index] = item + '-last';
		}); 
		node.attr('class', classList.join(' '));
		
		if(active) node.addClass('active');
		if(selected) node.addClass('selected');
		if(copied) node.addClass('copied');
		if(cut) node.addClass('cut');
		
		classList = null;  //release memory
	};
	
	/**
	 * Copy a cluster node to another node within the display cluster tree
	 */
	this.copyClusterNode = function(copyNode){
		this.clearLabelPathChanges();
		//get selected display node
		if(typeof copyNode == "undefined")
			copyNode = $('span.active', ulTree).parent();
		this.activeNode = copyNode;
		this.activeNodeOp = 'copy';
		copyNode = null;
		//tag node in tree as being copied
 		$('.copied', ulTree).removeClass('copied');
 		$('.cut', ulTree).removeClass('cut');
 		this.activeNode.addClass('copied');
	};
	
	/**
	 * Cut a cluster node from the display cluster tree
	 */
	this.cutClusterNode = function(cutNode){
		this.clearLabelPathChanges();
		//get selected display node
		if(typeof cutNode == "undefined")
			cutNode = $('span.active', ulTree).parent();
		this.activeNode = cutNode;
		this.activeNodeOp = 'cut';
		cutNode = null;
		$('.copied', ulTree).removeClass('copied');
 		$('.cut', ulTree).removeClass('cut');
 		this.activeNode.addClass('cut');
	};
	
	
	/**
	 * Paste a node that was either copied or cut
	 */
	this.pasteClusterNode = function(pasteTo){
		if(typeof pasteTo == "undefined")
			pasteTo = $('span.active', ulTree).parent();  //parent of span = li node
		var pathTo = pasteTo.attr('name');
		
		var pasteFrom = this.activeNode;
		var pathFrom = pasteFrom.attr('name');
		
		try {
			var newNode, cls;
			if(this.activeNodeOp == 'copy')
				newNode = this.copyNode(pathFrom, pathTo);
			else if(this.activeNodeOp == 'cut')
				newNode = this.moveNode(pathFrom, pathTo);
			
			//make display tree changes

			//pasting into the ul container below the pasteTo li node
			var pasteContainer = $('>ul', pasteTo);
			//transform last child of node we are pasting to into normal child
			this.makeNodeNotLast( this.getLastChild( pasteContainer ) );
			//add sub-tree for newly created node
			pasteContainer.append( newNode.toHTML(true) );
			if(this.activeNodeOp == 'cut') {
				//if cutting, and the node cut was -last, then transform its neighbor to -last
				cls = pasteFrom.attr('class');
				if(cls.indexOf('-last') > -1)
					this.makeNodeLast(pasteFrom.prev('li'));
				//remove the pasteFrom node
				pasteFrom.remove();
			}
			
			//add event handlers for the new nodes
			var newPath = newNode.getPathString();
			var newNodeElem = $("li[name='" + newPath + "']", pasteContainer);
			
			this.addNodeEvents(newNodeElem, true);
			
			//if node pasting to is closed, it should be opened
			cls = pasteTo.attr('class');
			if(cls.indexOf('-closed') > -1)
				this.nodeToggle(pasteTo);
			
			//update path labels along the new path
			this.updatePathNodeLabels(pathTo);
			
			if(this.activeNodeOp == 'cut') {
				//update path labels along the parent path of the old node
				var pasteFromParentPath = pathFrom.substring(0, pathFrom.lastIndexOf('.'));
				this.updatePathNodeLabels( pasteFromParentPath );

				//if cutting, and the old cut node was selected,
				//mark the new cluster node position to be selected
				if(pathFrom == this.displayedPagesNodePath) {
					newNodeElem.addClass('selected');
					this.displayedPagesNodePath = newPath;
				} else if(pathFrom.indexOf(this.displayedPagesNodePath) > -1){
					//if displayed node is parent of the cut node, re-display pages
					this.showNodePages(this.displayedPagesNodePath);
				}
				
				//check to see if parent of pasteFrom cluster is now empty
				//if it is, transform to bottom node
				if(pathFrom.lastIndexOf('.') > 2){ //no need to check if 1st level
					var pasteFromParent = this.root.getPathNode( pasteFromParentPath );
					if(!pasteFromParent.hasChildren()) {
						this.convertToBottomNode($("li[name='" + pasteFromParentPath + "']", ulTree));
						if(this.displayedPagesNodePath == pasteFromParentPath)
							//if former parent of cut node was selected and it has no children left
							//show 0 pages warning
							resDiv.html( this.emptyClusterWarning );
						
					}
					pasteFromParent = null;
				}
				
				//show completion message
				this.showTreeMessage('Label node moved successfully!', 'success');
				
				//reset operation
				this.activeNode = null;
				this.activeNodeOp = '';
				pasteFromParentPath = null;
				
				
				$('.cut', ulTree).removeClass('cut');  //node no longer cut
			} else {
				//show completion message
				this.showTreeMessage('Label node copied successfully!', 'success');
			}
			//if displayed node is parent of the node pasting into, re-display pages
			if(pathTo.indexOf(this.displayedPagesNodePath) > -1)
				this.showNodePages(this.displayedPagesNodePath);
				
			newNode = null;  //release memory
			cls = null;
			pasteContainer = null;
			newPath = null;
			newNodeElem = null;
			
		} catch (e){
			var message = e instanceof this.ecTreeOpException ? e.getMessage() : e;
			this.showTreeMessage(message, 'error');
			message = null;  //release memory
		}
		
		pasteTo = null;  //release memory
		pathTo = null;
		pasteFrom = null;
		pathFrom = null;
	};
	
	/**
	 * Initiate rename for a node label
	 */
	this.renameClusterNode = function(){
		
		
		this.clearLabelPathChanges();
		var renameNode = $('span.active', ulTree).parent();  //parent of span = li node
		var renamePath = renameNode.attr('name');
		
		if(this.activeRenameLabel != null){  //remove previous rename label if present
			$('#ecTreeInput').prev('span').html(this.activeRenameLabel);
		    $('#ecTreeInput').remove();
		    this.activeRenameLabel = null;
		}
		
		var node = this.root.getPathNode(renamePath);
		this.activeRenameLabel = $('>span', renameNode).html();
		$('>span', renameNode).html('').after('<input type="text" id="ecTreeInput" value="' + 
			node.getLabel() + '" maxlength="' + this.maximumLabelLength + '">');

		//adjust the width of the input according to the indentation of the level
		if(node.getLevel() > 1)
			$('#ecTreeInput').width( $('#ecTreeInput').width() - (24 * (node.getLevel() - 1)) );
		
		$('#ecTreeInput').focus().bind("keypress",
			 function(evt)
			 {
				 if (evt.keyCode == 13) { //pressed enter
					var label = $('#ecTreeInput').attr('value'); 	
					var path = $('#ecTreeInput').parent().attr('name');
				 	
				 	Tree.renameClusterNodeCallback (path, label);
				 	label = null;
				 	path = null;
				 	Tree.activeRenameLabel = null;
				 	
					//reset dragging status
					Tree.dragAllowed = Tree.dragSetting;
				 }
				 else if (evt.keyCode == 27) { // pressed esc
				    $('#ecTreeInput').prev('span').html(Tree.activeRenameLabel);
				    $('#ecTreeInput').remove();
				    Tree.activeRenameLabel = null;
				    
					//reset dragging status
				    Tree.dragAllowed = Tree.dragSetting;
				 }
			 }
		);
		
		//if the user clicks away form the field, execute renaming
		$('#ecTreeInput').blur(function(){
			var label = $('#ecTreeInput').attr('value'); 	
			var path = $('#ecTreeInput').parent().attr('name');
		 	
		 	Tree.renameClusterNodeCallback (path, label);
		 	label = null;
		 	path = null;
		 	Tree.activeRenameLabel = null;
		 	
			//reset dragging status
			Tree.dragAllowed = Tree.dragSetting;
		});
		
		//disable dragging during this action
		this.dragSetting = this.dragAllowed;
		this.dragAllowed = false;
		
		renameNode = null; //release memory
		renamePath = null;
		node = null;
	};
	
	/**
	 * Callback for renaming a node label
	 */
	this.renameClusterNodeCallback = function(nodePath, label){
		try {
			var node = this.renameNode(nodePath, label);
			label = node.getDisplayLabel();
			node = null;
			//show label and remove input
			$('#ecTreeInput').prev('span').html(label);
		    $('#ecTreeInput').remove();
		    //show completion message
			this.showTreeMessage('Label renamed successfully!', 'success');
		} catch(e){
			//try to restore old label
			try {
				label = this.root.getPathNode(nodePath).getLabel();
			} catch (e) { }
			var message = e instanceof this.ecTreeOpException ? e.getMessage() : e;
			this.showTreeMessage(message, 'error');
			message = null;
			//show label and remove input
			$('#ecTreeInput').prev('span').html(label);
		    $('#ecTreeInput').remove();
		}
		
	};

	
	/**
	 * Add a cluster node to the display cluster tree
	 */
	this.addClusterNode = function(bottomNode){
		this.clearLabelPathChanges();
		var addToNode = $('span.active', ulTree).parent();  //parent of span = li node
		var addToPath = addToNode.attr('name');
		
		if(this.activeRenameLabel != null){  //remove previous rename label if present
			$('#ecTreeInput').prev('span').html(this.activeRenameLabel);
		    $('#ecTreeInput').remove();
		    this.activeRenameLabel = null;
		}
		
		var node = this.root.getPathNode(addToPath);
		this.activeRenameLabel = null;
		
		//if node pasting to is closed, it should be opened
		cls = addToNode.attr('class');
		if(cls.indexOf('-closed') > -1)
			this.nodeToggle(addToNode);
		
		//add input box for entering node label
		if(bottomNode)
			$('>ul', addToNode).append('<li class="bottom-node-last" name="addNode">' +
				'<span class="label"></span><input type="text" id="ecTreeInput" value=""' + 
				' maxlength="' + this.maximumLabelLength + '"></li>');
		else 
			$('>ul', addToNode).append('<li class="cluster-open-last" name="addNode">' +
				'<span class="label"></span><input type="text" id="ecTreeInput" value=""' + 
				' maxlength="' + this.maximumLabelLength + '"><ul style="display: block;"></ul></li>');
		var addedNode = $("li[name='addNode']", addToNode);
		//transform its neighbor to not -last
		this.makeNodeNotLast(addedNode.prev('li'));
		
		//adjust the width of the input according to the indentation of the level
		if(node.getLevel() > 0)
			$('#ecTreeInput').width( $('#ecTreeInput').width() - (24 * ( node.getLevel() )) );
		
		$('#ecTreeInput').focus().bind("keypress",
			 function(evt)
			 {
				 if (evt.keyCode == 13) { //pressed enter
					var label = $('#ecTreeInput').attr('value'); 	
					var path = $('#ecTreeInput').parent().parent().parent().attr('name');
				 	
				 	Tree.addClusterNodeCallback (path, label);
				 	label = null;
				 	path = null;
				 	Tree.activeRenameLabel = null;

					//reset dragging status
				 	Tree.dragAllowed = Tree.dragSetting;
				 }
				 else if (evt.keyCode == 27) { // pressed esc
					var addedNode = $('#ecTreeInput').parent();
					//transform its neighbor to -last
					Tree.makeNodeLast(addedNode.prev('li'));
				    addedNode.remove();
				    addedNode = null;
				    Tree.activeRenameLabel = null;

					//reset dragging status
				    Tree.dragAllowed = Tree.dragSetting;
				 }
			 }
		);
		
		//if user clicks away from the field, execute add
		$('#ecTreeInput').blur(function(){
			var label = $('#ecTreeInput').attr('value'); 	
			var path = $('#ecTreeInput').parent().parent().parent().attr('name');
		 	
		 	Tree.addClusterNodeCallback (path, label);
		 	label = null;
		 	path = null;
		 	Tree.activeRenameLabel = null;

			//reset dragging status
		 	Tree.dragAllowed = Tree.dragSetting;
		});
		
		//disable dragging during this action
		this.dragSetting = this.dragAllowed;
		this.dragAllowed = false;
		
		addToNode = null; //release memory
		addToPath = null;
		node = null;
	};
	
	
	/**
	 * Callback for adding a cluster tree node
	 */
	this.addClusterNodeCallback = function(parentNodePath, label){
		try{
			var node = this.addNode(label, parentNodePath);
			label = node.getDisplayLabel();
			//show label and remove input
			$('#ecTreeInput').prev('span').html(label);
			var elem = $('#ecTreeInput').parent();
			var cls = elem.get(0).className;
			if( cls.indexOf('cluster') > -1 ){
				//this is not a bottom node, but rather an empty internal node
				node.setChildren(new Array());
			}
			elem.attr('name', node.getPathString());
		    $('#ecTreeInput').remove();
		    this.addNodeEvents(elem, true);
		    node = null;
		    elem = null;

		    //show completion message
			this.showTreeMessage('Label added successfully!', 'success');
		} catch(e){
			var message = e instanceof this.ecTreeOpException ? e.getMessage() : e;
			this.showTreeMessage(message, 'error');
			message = null;
			
			var addedNode = $('#ecTreeInput').parent();
			//transform its neighbor to -last
			Tree.makeNodeLast(addedNode.prev('li'));
		    addedNode.remove();
		    addedNode = null;
		}
		
	};
	
	
	/**
	 * Delete a cluster tree node
	 */
	this.deleteClusterNode = function(){
		var delNode = $('span.active', ulTree).parent();  //parent of span = li node
		var delPath = delNode.attr('name');
		
		try {
			var parentNode = this.removeNode(delPath);
			
			//if the node deleted was -last, then transform its neighbor to -last
			var cls = delNode.attr('class');
			if(cls.indexOf('-last') > -1)
				this.makeNodeLast(delNode.prev('li'));

			//remove the delNode node
			delNode.remove();
			
			var delPathParentPath = delPath.substring(0, delPath.lastIndexOf('.'));
			this.updatePathNodeLabels( delPathParentPath );
			
			
			//check to see if parent of delPath cluster is now empty
			//if it is, transform to bottom node
			if(delPath.lastIndexOf('.') > 2){ //no need to check if 1st level
				var delPathParent = this.root.getPathNode( delPathParentPath );
				if(!delPathParent.hasChildren())
					this.convertToBottomNode($("li[name='" + delPathParentPath + "']", ulTree));
				
				delPathParent = null;
			}
			
			//if selected node was just deleted, re-select parent node
			if(delPath == this.displayedPagesNodePath)
				this.showNodePages(delPathParentPath);
			else if(delPath.indexOf(this.displayedPagesNodePath) > -1){
				//if displayed node is parent of the deleted node, re-display pages
				this.showNodePages(this.displayedPagesNodePath);
			}
			
			//show completion message
			this.showTreeMessage('Label node deleted successfully!', 'success');
			
			parentNode = null;  //release memory
			cls = null;
			delPathParentPath = null;
		} catch (e){
			var message = e instanceof this.ecTreeOpException ? e.getMessage() : e;
			this.showTreeMessage(message, 'error');
			message = null;
		}
		delNode = null;  //release memory
		delPath = null;
		
		this.activeNode = null; //invalidate any previous copy node operation
		this.activeNodeOp = '';
		$('.copied', ulTree).removeClass('copied');
 		$('.cut', ulTree).removeClass('cut');
	};
	
	/**
	 * Copy a result node for pasting elsewhere
	 */
	this.copyResultPage = function(page){
		this.clearLabelPathChanges();
		if(typeof page == "undefined")
			page = $('div.active-page>div', resDiv);
		this.activePage = page;
		this.activePageNodePath = this.displayedPagesNodePath;
		this.activePageOp = 'copy';
		page = null;
		
		//tag page as being copied
		$('.copied-page', resDiv).removeClass('copied-page');
 		$('.cut-page', resDiv).removeClass('cut-page');
 		this.activePage.parent().addClass('copied-page');
	};
	
	/**
	 * Cut a result node for pasting elsewhere
	 */
	this.cutResultPage = function(page){
		this.clearLabelPathChanges();
		var pathFrom = this.displayedPagesNodePath;
		
		if(typeof page == "undefined")
			page = $('div.active-page>div', resDiv);
		this.activePage = page;
		this.activePageNodePath = this.displayedPagesNodePath;
		this.activePageOp = 'cut';
		page = null;
		
		//tag page as being copied
 		$('.copied-page', resDiv).removeClass('copied-page');
 		$('.cut-page', resDiv).removeClass('cut-page');
 		this.activePage.parent().addClass('cut-page');
		
		pathFrom = null;  //release memory
		deleteFromNode = null;
	};
	
	/**
	 * Paste a result node as a result of a copy or paste operation
	 */
	this.pasteResultPage = function(pasteTo){
		if(typeof pasteTo == "undefined")
			pasteTo = $('span.active', ulTree).parent();  //parent of span = li node
		var pathTo = pasteTo.attr('name');
		
		var pageId = this.activePage.attr('name');
		var pathFrom = this.activePageNodePath;
		
		try {
			if(this.activePageOp == 'copy')
				this.copyResult(pathFrom, pathTo, pageId);
			else if(this.activePageOp == 'cut')
				this.moveResult(pathFrom, pathTo, pageId);
			
			//execute display tree changes
			var pasteToNode = this.root.getPathNode(pathTo);
			//update the label of the pasteToNode and all parents
			this.updatePathNodeLabels(pathTo);
			
			if(this.activePageOp == 'cut'){
				//get node to delete from
				var cutFromNode = this.root.getPathNode(pathFrom);
				
				//update the label of the cutFromNode and all parents
				this.updateChildPathNodeLabels(pathFrom);
				
				//delete the page from the results section
				this.activePage.parent().remove();
				
				//show completion message
				this.showTreeMessage('Result node moved successfully!', 'success');
				
				//if no pages remaining in node, show warning
				if($('.entry', resDiv).length == 0)
					resDiv.html( this.emptyClusterWarning );
				
				//reset operation
				this.activePage = null;
				this.activePageOp = '';
				
				cutFromNode = null;  //release memory
			} else {
				//show completion message
				this.showTreeMessage('Result node copied successfully!', 'success');
				
			}
			
			//if displayed node is parent of the node pasting into, re-display pages
			if(pathTo.indexOf(this.displayedPagesNodePath) > -1)
				this.showNodePages(this.displayedPagesNodePath);
			
			pasteToNode = null;  //release memory
			
		} catch (e){
			var message = e instanceof this.ecTreeOpException ? e.getMessage() : e;
			this.showTreeMessage(message, 'error');
			message = null;  //release memory
		}
		
		pasteTo = null;  //release memory
		pathTo = null;
		pageId = null;
		pathFrom = null;
		
	};
	
	/**
	 * Delete a result node / page
	 */
	this.deleteResultPage = function(){
		var pageNode = $('div.active-page>div', resDiv);
		var pageId = pageNode.attr('name');
		var pathFrom = this.displayedPagesNodePath;
		
		try {
			this.removeResult(pathFrom, pageId);
			
			//get node to delete from
			var removeFromNode = this.root.getPathNode(pathFrom);
			
			//update the label of the removeFromNode and all parents and children
			this.updateChildPathNodeLabels(pathFrom);
			
			
			//delete the page from the results section
			pageNode.parent().remove();
			
			//show completion message
			this.showTreeMessage('Result node removed successfully!', 'success');
			
			//if no pages remaining in node, show warning
			if($('.entry', resDiv).length == 0)
				resDiv.html( this.emptyClusterWarning );
			
			removeFromNode = null;
			
		} catch (e){
			var message = e instanceof this.ecTreeOpException ? e.getMessage() : e;
			this.showTreeMessage(message, 'error');
			message = null;  //release memory
		}
		
		this.activePage = null; //invalidation possible previous page copy operation
		this.activePageOp = '';
		
		pageNode = null;  //release memory
		pageId = null;
		pathFrom = null;
		index = null;
		deleteFromNode = null;
		
	};
	
	/**
	 * Update labels of all nodes along a path (generally due to the fact that cluster
	 * sizes have changed and need to be updated)
	 */
	this.updatePathNodeLabels = function(path){
		var pathParts = path.split('.');
		var elem = ulTree;
		var node = this.root;
		var ps = node.getPathString();
		elem = $("li[name='" + ps + "']", elem);
		elem.children().filter('span').html( node.getDisplayLabel() );
		for(var j = 1; j < pathParts.length; j++){
			node = node.getChild(pathParts[j]);
			if(node == null) break;
			ps = node.getPathString();
			elem = $("li[name='" + ps + "']", elem);
			if(elem.length == 0) break;
			elem.children().filter('span').html( node.getDisplayLabel() );
		}
		ps = null; //release memory
		pathParts = null;
		elem = null;
		
		return node;
	};
	
	/**
	 * Update labels of all nodes along a path (generally due to the fact that cluster
	 * sizes have changed and need to be updated) + all the children paths from the current
	 * node
	 * - node is parent node recursively passed to children by parents
	 * - elem is parent DOM element (wrapped by jQuery) object - to simplify search of child DOM elem
	 */
	this.updateChildPathNodeLabels = function(path, node, elem){
		//update path nodes from root to current
		if(typeof node == "undefined")
			node = this.updatePathNodeLabels(path);
		var ps;
		//update child paths
		if(node != null){
			ps = node.getPathString();
			elem = $("li[name='" + ps + "']", elem);
			if(elem.length == 0) return false;
			elem.children().filter('span').html( node.getDisplayLabel() );
			if(node.hasChildren()){
				var children = node.getChildren();
				for(c in children)
					this.updateChildPathNodeLabels(null, children[c], elem);
				children = null;
			}
		}
		return node;
	};
	
	
	/**
	 * Add context menus to the page
	 */
	if($('#ecTreeMenu').length == 0) {
		$('body').append(this.contextMenuDiv);
		$('body').append(this.disabledEditContextMenuDiv);
		$('body').append(this.browseTInitContextMenuDiv);
		//bind click events for all menu actions
		$('#ecTreeMenu .addNode').click(function(){  Tree.addClusterNode(false); });
		$('#ecTreeMenu .addBottomNode').click(function(){  Tree.addClusterNode(true); });
		$('#ecTreeMenu .copyNode').click(function(){  Tree.copyClusterNode(); });	
		$('#ecTreeMenu .copyResult').click(function(){  Tree.copyResultPage(); });	
		$('#ecTreeMenu .cutNode').click(function(){  Tree.cutClusterNode(); });	
		$('#ecTreeMenu .cutResult').click(function(){  Tree.cutResultPage(); });	
		$('#ecTreeMenu .pasteNode').click(function(){  Tree.pasteClusterNode(); });	
		$('#ecTreeMenu .pasteResult').click(function(){  Tree.pasteResultPage(); });	
		$('#ecTreeMenu .rename').click(function(){  Tree.renameClusterNode(); });	
		$('#ecTreeMenu .deleteNode').click(function(){  Tree.deleteClusterNode(); });
		$('#ecTreeMenu .deleteResult').click(function(){  Tree.deleteResultPage(); });
		$('#ecTreeMenu .clearAllEdits').click(function(){ Tree.clearAllEdits(); });
		$('#ecTreeMenu .showWithoutEdits').click(function(){ Tree.executeSearch(); });
	}
	if(this.dragAllowed == true) {
		if($('#ectDragContainer').length == 0)
			$('body').append('<div id="ectDragContainer"><ul class="ecTree"></ul></div>');
		$('#ectDragContainer').hide();
		if($('#ectTreePlus').length == 0)
			$("<img>").attr({
				id	: "ectTreePlus",
				src	: "includes/images/tree/plus.gif"
			}).css({
				width   : "7px",
				display : "block",
				position: "absolute",
				left	: "5px",
				top     : "5px", 
				display :'none'
			}).appendTo("body");
		$('#ectTreePlus').hide();
	}
	
	//return reference to the editableClusterTree object
	return this;
}

/**
 * Set a message in a div that will time out after a certain amount of time
 *
 * @param divId: string - id of div to set the message in
 * @param message: string - message to set
 * @param ttl: int - time to live in miliseconds
 * @param cls: string - optional class to add to div
 *
 * @return void
 */
 function setDivMessage(divId, message, ttl, cls){
   $('#' + divId).removeClass('error');
   $('#' + divId).removeClass('success');
   $('#' + divId).html(message);
   var time = new Date();
   time = time.getTime();
   ectMessageTimeout[divId] = time;
   if(typeof ttl == "number") setTimeout("clearDivMessage('" + divId + "','" + time + "')", ttl);
   if(typeof cls == "string") $('#' + divId).addClass(cls);
   time = null; //release memory
 }
 
 /**
 * Remove a message previously set in a div with a timeout
 *
 * @return void
 */
 function clearDivMessage(divId, time){
   if(ectMessageTimeout[divId] == time){
     $('#' + divId).html('&nbsp;');
     $('#' + divId).removeClass('error');
     $('#' + divId).removeClass('success');
   }
 }

//data store for setDivMessage / clearDivMessage
var ectMessageTimeout = {};

$(window).unload(function() {
	ectMessageTimeout = null;
});
