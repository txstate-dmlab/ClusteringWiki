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
 * Bring up menu and footer items fast
 */
$().ready(function() {
	$("#topBarLeft").fadeIn(300);
	$("#topBarRight").fadeIn(300);
	$("#footer").fadeIn(300);
});


/**
 * Add an onEnter event to an input field.
 * @author David C. Anastasiu
 * @since 04 Apr 2010
 * @return void
 */
function onEnter(field,methodCall){
    $("#"+field).keyup(function(e){
        if(e.keyCode==13){
            try{
                eval(methodCall+"()");
            }catch(exception){}
        }
    });
}