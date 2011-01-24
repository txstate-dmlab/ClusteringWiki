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
 
 //global instance of the editableClusterTree obj
var ect;
//global instance of current test details, if any
var testExecution;

RegExp.escape = function(text) {
    return text.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
};


/**
 * Actions to occur on page load
 * @author David C. Anastasiu
 * @since 13 Jul 2010
 */
$().ready(function() {
	// add onEnter event to deal with submit inconsistencies
	onEnter('query', 'processQ');
	
	$("#topBarLeft").fadeIn(300);
	$("#topBarRight").fadeIn(300);
	$("#footer").fadeIn(300);
	
	//Make hov images change when hovering over them
    $('.hovimg').mouseover(hovImgOver);
    $('.hovimg').mouseout(hovImgOut);
	
    //enable display of cluster edits when checking 'Show cluster edits' check-box
    $('#showEdits').click(function(){
    	$('#showTimes').attr('checked', false);
    	$('#clusterTimes').hide();
    	$('#showQueries').attr('checked', false);
    	$('#clusterQueries').hide();
		if($('#showEdits').attr('checked'))
			$('#clusterEdits').show();
		else
			$('#clusterEdits').hide();
    });
    //deal with FF keeping check-boxes checked after page refresh
    if($('#showEdits').attr('checked'))
		$('#clusterEdits').show();
    
    //enable display of clustering times when checking 'Show times' check-box
    $('#showTimes').click(function(){
    	$('#showEdits').attr('checked', false);
    	$('#clusterEdits').hide();
    	$('#showQueries').attr('checked', false);
    	$('#clusterQueries').hide();
		if($('#showTimes').attr('checked'))
			$('#clusterTimes').show();
		else
			$('#clusterTimes').hide();
    });
    //deal with FF keeping check-boxes checked after page refresh
    if($('#showTimes').attr('checked'))
		$('#clusterTimes').show();

    //enable display of clustering times when checking 'Show times' check-box
    $('#showQueries').click(function(){
    	$('#showEdits').attr('checked', false);
    	$('#clusterEdits').hide();
    	$('#showTimes').attr('checked', false);
    	$('#clusterTimes').hide();
		if($('#showQueries').attr('checked')){
			getLatestMostPopularQueries();
			$('#clusterQueries').show();
		} else
			$('#clusterQueries').hide();
    });
    //deal with FF keeping check-boxes checked after page refresh
    if($('#showQueries').attr('checked')){
		getLatestMostPopularQueries();
		$('#clusterQueries').show();
    }
    
    //if Google source is selected, change count to 50 and disable remaining values
    $('#engine').change(function(){
    	if($(this).val() == 'google'){
    		$('#count > option').each(function(i){
    			if($(this).val() != '50')
    				$(this).attr('disabled', 'disabled');
    		});
			$('#count').val('50');
    	} else {
    		$('#count > option').each(function(){
    			$(this).attr('disabled', false);
    		});
    	}
    });
    //initial disable of google count values
    if($('#engine').val() == 'google'){
		$('#count > option').each(function(i){
			if($(this).val() != '50')
				$(this).attr('disabled', 'disabled');
		});
		$('#count').val('50');
	}
    
    ect = new editableClusterTree({
        debugDivElementId: 'clusterEditsMsg', //where to place debug info, comment out to display in console only
        dragAllowed: true,                    //enable drag-and-drop moving
        highlightFunction: function(labels, text){
    		if(text.length < 2) return text;
    		text = text.split(/\s+/);
    		for(var i in labels){
    			var labelWords = labels[i].split(/\s+/);
    			for(var j in labelWords){
    				if(labelWords[j].length > 4){
    					labelWords[j] = stemmer(labelWords[j]);
    				}
    				try {
	    				var m = new RegExp(RegExp.escape(labelWords[j]), 'i');
	    				for(var k in text){
	    					var tWord = text[k];
	    					if(tWord.length > 0 && tWord.charAt(0) != '<' && tWord.match(m) != null)
	    						text[k] = '<b>' + text[k] + '</b>';
	    				}
    				} catch (e) { /* do nothing */ }
    			}
    		}
    		return text.join(' ');
    	}
    });
    
    $(window).unload(function() {
    	ect = null;
    });

    
});


/**
* Execute a search using the ect object
*/
function processQ(startAt){
	var query = $('#query').val();
	if(typeof startAt != "number") startAt = 1;
	var engine = $('#engine').val();
	var itemsPerPage = $('#count').val();
	var lastResult = parseInt(startAt) + parseInt(itemsPerPage);
	var clusteringAlgo = $('#clusteringAlgo').val();
	
	ect.executeSearch(startAt, query, engine, itemsPerPage, lastResult, clusteringAlgo);
	
	query = null;
	engine = null;
	itemsPerPage = null;
	lastResult = null;
	clusteringAlgo = null;
}

/**
 * Retrieve the latest most popular queries and populate in the 
 * clusterQueriesMsg pre element
 * @return
 */
function getLatestMostPopularQueries(){
	$('#clusterQueriesMsg').html('');
	$('#clusterQueriesMsg').removeClass('error')
	.html('Please wait... <img src="includes/images/loading.gif">');
	
	var l = window.location;
	url = l.protocol + '//' + l.hostname;
	if(l.port != 80) url += ':' + l.port;
	url += '/' + l.pathname.substring(1, l.pathname.lastIndexOf('/'));
	l = null;
	url += '/rest/getMostPopularQueries';
	
	$.ajax({
        url: url,	
        type: 'post',
		async: true,
		dataType: 'json',
		success: function(data){
		    if(typeof data == "object" && data.success == true){
		    	var queries = data.queries;
		    	var txt = '';
		    	if(queries.length == 0){
		    		$('#clusterQueriesMsg').html('No queries have yet been edited.');
					$('#clusterQueriesMsg').addClass('error');
		    	} else {
			    	for(var i in queries){
			    		txt += '<a href="javascript:invokePopularQuery(\'' + queries[i] + '\')">' +
			    		 	queries[i] + '</a>' + '<br>';
			    	}
			    	$('#clusterQueriesMsg').html(txt);
		    	}
		    	queries = null;
		    	txt = null;
		    } else {
		    	var err = 'Invalid server response.';
		    	if(typeof data.error) err = data.error;
		    	$('#clusterQueriesMsg').html(err);
		        $('#clusterQueriesMsg').addClass('error');
		        err = null;
		    }
		},
		error: function(oXHR, status){
			$('#clusterQueriesMsg').html('Server connection error.  Please try again.');
			$('#clusterQueriesMsg').addClass('error');
		}
    });
	
}

/**
 * Invoke a popular query that was clicked on
 * @param query
 * @return
 */
function invokePopularQuery(query){
	$('#query').val(query);
	$('#engine').val('google');
	$('#count').val(50);
	$('#clusteringAlgo').val(3);
	processQ(1);
}


/**
 * Start or resume test
 * @param executionId
 * @return
 */
function startTest(executionId){
	$('#r1').hide();
	$('#r2').hide();
	$('#searchButton').hide();
	$('#testNext').hide();
	$('#remainingTagCount').hide();
	$('#query').hide();
	$('#queryTest').val('');
	$('#testMessagesMsg').removeClass('error')
		.html('Please wait while we prepare the test. <img src="includes/images/loading.gif">');
	$('#testMessages').show();
	ect.clearTags();
	ect.clearCurrentSearch();
	
	var l = window.location;
	url = l.protocol + '//' + l.hostname;
	if(l.port != 80) url += ':' + l.port;
	url += '/' + l.pathname.substring(1, l.pathname.lastIndexOf('/'));
	l = null;
	url += '/rest/test/get/' + executionId + '.json';
	
	$.ajax({
        url: url,	
        type: 'post',
		async: true,
		dataType: 'json',
		success: function(data){
		    if(typeof data == "object" && data.success == true){
		    	if('logIn' in data){
		    		//User should log in or log out...
		    		$('#testMessagesMsg').html(data.description);
			        $('#testMessagesMsg').addClass('error');
		    	} else if('done' in data){
		    		//User should log in or log out...
		    		$('#testMessagesMsg').html(data.description);
			        $('#testMessagesMsg').addClass('success');
		    	} else {
		    		//normal step execution
		    		var info = '';
		    		if(data.description != '')
		    			info += ' &nbsp; ' + data.description;
		    		if(data.enableTagging > 0){
		    			info += ' Please tag ' + data.tagCount + ' result';
		    			if(data.tagCount > 1) info += 's';
		    			info += ' before continuing to the next step.';
		    			$('#remainingTagCount').html(data.tagCount + ' remaining tags.');
		    			$('#remainingTagCount').show();
		    		}
		    		info += '<br><br>';
		    		if(data.narrative != '')
		    			info += ' &nbsp; Narrative: ' + data.narrative + '<br>';
		    		//enable or disable tagging
		    		if(data.enableTagging > 0)
		    			ect.enableTagging(true);
		    		else 
		    			ect.enableTagging(false);
		    		if(data.enableTagging)
		    			ect.setTagCount(data.tagCount);
		    		//enable or disable editting
		    		if(data.loggedIn == 1){
			    		if(data.disableEditting > 0){
			    			ect.setEditAllowed(false);
			    			ect.setDisabledMessage('Editing has been temporarily disabled for this step.');
			    		} else {
			    			ect.setEditAllowed(true);
			    		}
		    		}
		    		//set query parameters
		    		if(data.source != '')
		    			$('#engine').val(data.source);
		    		if(data.results > 0)
		    			$('#count').val(data.results);
		    		if(data.algorithm < 4)
		    			$('#clusteringAlgo').val(data.algorithm);
		    		//show message
		    		$('#testMessagesMsg').html(info);
		    		$('#testNext').show();
		    		testExecution = ect.clone(data);
		    		testExecution['message'] = info;
		    		//execute query
		    		if(data.query != ''){
			    		$('#query').val(data.query);
			    		if(data.query.indexOf('topic:') > -1){
			    			var q = jQuery.trim(data.query);
			    			q = q.substring(q.indexOf(' ') + 1);
			    			$('#queryTest').val(q);
			    			q = null;
			    		} else {
			    			$('#queryTest').val(data.query);
			    		}
			    		$('#queryTest').show();
			    		if(data.hideCluster > 0){
			    			$('#cluster').hide();
			    		} else {
			    			$('#cluster').show();
			    		}
			    		processQ();
		    		}
		    	}
		    } else {
		    	var err = 'Invalid server response.';
		    	if(typeof data.error) err = data.error;
		    	$('#testMessagesMsg').html(err);
		        $('#testMessagesMsg').addClass('error');
		        err = null;
		    }
		},
		error: function(oXHR, status){
			$('#testMessagesMsg').html('Server connection error.  Please try again.');
			$('#testMessagesMsg').addClass('error');
		}
    });
}

/**
 * Validate that this step has been completted, 
 * send data to server, and initiate next test step
 * @return
 */
function nextTestStep(){
	//first validate that the required number of tags have been selected, if necessary
	if(testExecution.enableTagging > 0){
		var tags = ect.countTags();
		if(tags < testExecution.tagCount){
			$('#testMessagesMsg').html(testExecution.message +
				"\n   Please tag all required items before continuing to the next step.");
			return;
		}
		//send tags + execution times
		if(!sendExecutionInfo(true)) return;
	} else {
		//send execution times only
		if(!sendExecutionInfo(false)) return;
	}
	startTest(testExecution.executionId);
}

/**
 * Collect test information before moving on to the next step and
 * send it to the server
 * @param includeTags
 * @return
 */
function sendExecutionInfo(includeTags){
	//add final tag info
	ect.setTagExecutionInfo(true);
	var info = '{tagExecutionInfo:' + $.toJSON( ect.getTagExecutionInfo() ) + 
		',stepId:' + testExecution.stepId +
		',times:' + $.toJSON( ect.getExecutionTimes() ) +
		',cluster:' + ect.getInternalStructure() + '}';
	
	var l = window.location;
	url = l.protocol + '//' + l.hostname;
	if(l.port != 80) url += ':' + l.port;
	url += '/' + l.pathname.substring(1, l.pathname.lastIndexOf('/'));
	l = null;
	url += '/rest/test/put/' + testExecution['executionId'] + '.json';
	
	var ret = false;
	$.ajax({
        url: url,
        type: 'POST',
        contentType: 'multipart/form-data',
		async: false,
		data: info,
		processData: false,
		dataType: 'json',
		success: function(data){
		
		    if(typeof data == "object" && typeof data.error != "undefined"){
		    	$('#testMessagesMsg').html(data.error);
		        $('#testMessagesMsg').addClass('error');
		        ret = false;
		        data = null;
		        return;
		    } //otherwise info received successfully
		    
		    data = null;
		    ret = true;
		},
		error: function(oXHR, status){
			$('#testMessagesMsg').html('Server connection error.  Please try again.');
			$('#testMessagesMsg').addClass('error');
			ret = false;
		}
    });
	return ret;
}

/**
 * Add an onEnter event to an input field.
 * @author David C. Anastasiu
 * @since 04 Apr 2010
 * @return void
 */
function onEnter(field, methodCall){
    $("#" + field).keyup(function(e){
        if(e.keyCode == 13){
            try{
                eval(methodCall + "()");
            } catch(exception){}
        }
    });
}

/**
 * Event action for hover images activated when mouse hovers over image
 *
 * @return void
 */
function hovImgOver(img){
	var imgSrc, ext;
	if(typeof img.src == "undefined"){
		imgSrc = this.src;
		ext = imgSrc.substr(imgSrc.lastIndexOf("."));
		if(imgSrc.indexOf("_hov") == -1) this.src = imgSrc.replace(ext, "_hov" + ext);
	} else {
		imgSrc = img.src;
		ext = imgSrc.substr(imgSrc.lastIndexOf("."));
		if(imgSrc.indexOf("_hov") == -1) img.src = imgSrc.replace(ext, "_hov" + ext);
	}
	imgSrc = null;
	ext = null;
 }
 
 /**
 * Event action for hover images activated when mouse stops hovering over image
 *
 * @return void
 */
 function hovImgOut(img){
	 var imgSrc;
	 if(typeof img.src == "undefined"){
		 imgSrc = this.src;
		 this.src = imgSrc.replace("_hov", "");
	 } else {
		 imgSrc = img.src;
		 img.src = imgSrc.replace("_hov", "");
	 }
	 imgSrc = null;
 }