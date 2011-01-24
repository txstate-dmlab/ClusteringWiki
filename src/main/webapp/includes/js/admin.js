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
 
 
$().ready(function() {	
	$("#settgingsForm").validate({
		rules: {
			topKQueryUrls: {
				required: true,
				maxlength: 3
			},
			maxClusteringIterations: {
				required: true,
				maxlength: 3
			},
			similarityCalculator: {
				required: true
			},
			termSimQueryResultsLimit: {
				required: true
			},
			termSimThreshold: {
				required: true
			},
			resultSimThreshold: {
				required: true
			}
		},
		messages: {
			
		}
		
	});	
	
	onEnter('topKQueryUrls', 'save');
	onEnter('maxClusteringIterations', 'save');
	
});


/**
 * Register user via ajax
 * @return void
 */
function save(){
	
	if(!$("#settgingsForm").valid()) return;
	
	$('#saveResponse').removeClass('error').removeClass('success')
		.html('Please wait...<img src="includes/images/loading.gif">');
	
	//get form values
	var timingEnabled = $('#timingEnabled').is(':checked') ? 'true' : 'false';
	var topKQueryUrls  = $('#topKQueryUrls').val();
    var maxClusteringIterations = $('#maxClusteringIterations').val();
    var similarityCalculator = $('#similarityCalculator').val();
    var termSimQueryResultsLimit = $('#termSimQueryResultsLimit').val();
    var termSimThreshold = $('#termSimThreshold').val();
    var resultSimThreshold = $('#resultSimThreshold').val();
    
    $.ajax({
        url: 'admin.html?applAction=saveSettings',	
        type: 'post',
		async: true,
		data: 'timingEnabled=' + timingEnabled + 
			'&topKQueryUrls=' + topKQueryUrls + 
			'&maxClusteringIterations=' + maxClusteringIterations + 
			'&similarityCalculator=' + similarityCalculator + 
			'&termSimQueryResultsLimit=' + termSimQueryResultsLimit + 
			'&termSimThreshold=' + termSimThreshold + 
			'&resultSimThreshold=' + resultSimThreshold,
		dataType: 'json',
		success: function(data){
		    if(typeof data == "object" && data.success == true){
		    		$('#saveResponse').html('Settings saved successfully.');
		    		$('#saveResponse').addClass('success');
		    } else {
		    	var err = ['Invalid server response.'];
		    	if(typeof data.errors) err = data.errors;
		    	$('#saveResponse').html(err.join('<br>'));
		        $('#saveResponse').addClass('error');
		        err = null;
		    }
		    timingEnabled = null;
			topKQueryUrls  = null;
		    maxClusteringIterations = null;
		    similarityCalculator = null;
		    termSimQueryResultsLimit = null;
		    termSimThreshold = null;
		    resultSimThreshold = null;
		},
		error: function(oXHR, status){
			$('#saveResponse').html('Server connection error.  Please try again.');
			$('#saveResponse').addClass('error');
		}
    });
    
}

