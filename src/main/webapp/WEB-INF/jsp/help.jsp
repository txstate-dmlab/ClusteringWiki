<!--
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
-->
 
<%@ include file="documentHeaders.jspf" %>
<script src="includes/js/menu.js" type="text/javascript"></script>
<%@ include file="banner.jspf" %>    
<link rel="stylesheet" href="includes/css/help.pack.css" type="text/css" media="screen" >
<!-- being index -->
	<div id="box">
		<div id="logo">
			<span id="logow"><fmt:message key="app.menuTitle"/></span>
		</div>
		<div id="title">
			<h2>Help</h2>
		</div>
	</div>		

	<div id="rpanel">
	<p>
		ClusteringWiki allows you to edit clusters of search results and share your edits with other users.  
		When logged into ClusteringWiki, you can personalize a search results cluster by editing the membership, 
		structure and labels of clusters.  If you issue a query while logged out you will be presented with an 
		aggregated results cluster tree containing community "voted" clusters.
	</p>
	<p>
		Issue a query and click "search".  Your search results will be presented in a clustered manner.  
		In ClusteringWiki, there are three types of cluster "nodes":
	</p>
	<table cellpadding="2" cellspacing="3" border="0" class="ecTree menuItems">
		<tr>
			<td width="140"> <strong>Root label node:</strong> <span class="root"></span> </td>
			<td> The root label node cannot be deleted or renamed.  Double-click expands subtrees or collapses to first children level.  Click the label to display all retrieved search results.</td>
		</tr>
		<tr>
			<td> <strong>Internal label node:</strong> <br><span class="cluster-open"></span> &nbsp; or &nbsp; <span class="cluster-closed"></span> </td>
			<td> Can contain both internal and bottom label nodes.  Click the + or - next to an internal label node to expand or collapse first children level.  Double-click to expand/collapse subtrees.</td>
		</tr>
		<tr>
			<td> <strong>Bottom label node:</strong> <span class="bottom-node-last"></span></td>
			<td> Can only contain result nodes.</td>
		</tr>
		<tr>
			<td> <strong>Result node</strong> </td>
			<td> Results are displayed on the right-side of the page in the order originally received from the search engine source.</td>
		</tr>
	</table>
	<p>
		<strong>Editing: </strong> &nbsp; You must first <a href="login.html">log in</a> to enable editing.  If you do not already have an account, 
		<a href="register.html">register</a> for one.  All you need is a valid email address.  
	</p>
	<p>
		Right-click on the cluster and result nodes 
		to edit result clusters.  The right-click menus will display context-sensitive actions.  For example, 
		the <br> " <img src="includes/images/tree/page_paste.png"> Paste result " menu item will only be shown if a result node
		was first cut or copied and you right-clicked on a bottom label node.  Let's explore your available context-menus:
	</p>
		
	<table cellpadding="2" cellspacing="3" border="0" class="menuItems">
		<tr>
			<td width="135"><strong>menu item</strong></td>
			<td width="80"><strong>node type where shown</strong></td>
			<td><strong>action</strong></td>
		</tr>
		
		<tr>
			<td> <img src="includes/images/tree/tree_clear_all_edits.png"> Clear all edits </td>
			<td> root </td>
			<td> Delete all stored edits for current cluster and re-load cluster.</td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/tree_without_edits.png"> Show tree w/o edits  </td>
			<td> root </td>
			<td> Re-load cluster in "browse-only" mode without any applied cluster edits.</td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/node_add.png"> Create internal label </td>
			<td> internal label </td>
			<td> Add a new label node. New label node level cannot exceed a set limit. Enter label name, then press ESC to cancel, ENTER to create.</td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/bottom_node_add.png"> Create bottom label  </td>
			<td> internal label </td>
			<td> Add a new bottom label node. New bottom label node level cannot exceed a set limit. Enter label name, then press ESC to cancel, ENTER to create. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/node_paste.png"> Paste label </td>
			<td> internal label </td>
			<td> Paste a copied or cut label. Only shown if a label node was copied or cut. If label was cut, a move operation is performed. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/node_copy.png"> Copy label </td>
			<td> internal and bottom label </td>
			<td> Copy label into memory buffer. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/node_cut.png"> Cut label </td>
			<td> internal and bottom label </td>
			<td> Copy label into memory buffer and mark for deletion when pasted. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/node_rename.png"> Rename label  </td>
			<td> internal and bottom label </td>
			<td> Rename a label. Enter new label name, then press ESC to cancel, ENTER to create.  </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/node_delete.png"> Delete label  </td>
			<td> internal and bottom label </td>
			<td> Delete a label node. Only shown if label cluster contains only results that have duplicates elsewhere in the tree. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/page_paste.png"> Paste result  </td>
			<td> bottom label </td>
			<td> Paste a copied or cut result. Only shown if a result was copied or cut. If result was cut, a move operation is performed. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/page_copy.png"> Copy result  </td>
			<td> result </td>
			<td> Copy result into memory buffer. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/page_cut.png"> Cut result  </td>
			<td> result </td>
			<td> Copy result into memory buffer and mark for deletion when pasted. </td>
		</tr>
		<tr>
			<td> <img src="includes/images/tree/page_delete.png"> Delete result  </td>
			<td> result </td>
			<td> Delete a result node. Only shown if the result node has at least one other copy elsewhere in the cluster tree, outside the currently selected label node. </td>
		</tr>
		
	</table>
	
	<br>
	
	<p>
		<strong>Drag and drop: </strong> &nbsp; Moving and copying nodes can also be accomplished by dragging a label or result to an internal 
		or bottom label node respectively.  Drag and drop executes a move operation by default. Hold down CTRL when you drop the node to execute
		a copy operation.  Keep in mind label nodes can only be moved/copied to internal label nodes (not bottom label nodes)
		and result nodes can only be moved/copied to bottom label nodes. When dragging, if a node is not dropped on an appropriate target node, 
		it will be returned to its starting place and the move/copy will be canceled.
	</p>
		
	<p>
		<strong>Other functions: </strong> &nbsp; Click on the "Show cluster edits" checkbox to see the edits that are being sent to the server 
		for the different editing operations you complete. Click the "Show execution times" checkbox to see the breakdown of the response time for the last issued query.
		Click the "Show popular queries" checkbox to see the queries that have been edited by most users. 
		Note that only one of these functions will be shown at a time.
	</p>
	
	<br><br><br><br>
	</div>
<!-- end index -->
<%@ include file="footer.jspf" %>  