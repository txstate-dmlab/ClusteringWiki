var ect;var testExecution;RegExp.escape=function(a){return a.replace(/[-[\]{}()*+?.,\\^$|#\s]/g,"\\$&")};$().ready(function(){onEnter("query","processQ");$("#topBarLeft").fadeIn(300);$("#topBarRight").fadeIn(300);$("#footer").fadeIn(300);$(".hovimg").mouseover(hovImgOver);$(".hovimg").mouseout(hovImgOut);$("#showEdits").click(function(){$("#showTimes").attr("checked",false);$("#clusterTimes").hide();$("#showQueries").attr("checked",false);$("#clusterQueries").hide();if($("#showEdits").attr("checked")){$("#clusterEdits").show()}else{$("#clusterEdits").hide()}});if($("#showEdits").attr("checked")){$("#clusterEdits").show()}$("#showTimes").click(function(){$("#showEdits").attr("checked",false);$("#clusterEdits").hide();$("#showQueries").attr("checked",false);$("#clusterQueries").hide();if($("#showTimes").attr("checked")){$("#clusterTimes").show()}else{$("#clusterTimes").hide()}});if($("#showTimes").attr("checked")){$("#clusterTimes").show()}$("#showQueries").click(function(){$("#showEdits").attr("checked",false);$("#clusterEdits").hide();$("#showTimes").attr("checked",false);$("#clusterTimes").hide();if($("#showQueries").attr("checked")){getLatestMostPopularQueries();$("#clusterQueries").show()}else{$("#clusterQueries").hide()}});if($("#showQueries").attr("checked")){getLatestMostPopularQueries();$("#clusterQueries").show()}$("#engine").change(function(){if($(this).val()=="google"){$("#count > option").each(function(a){if($(this).val()!="50"){$(this).attr("disabled","disabled")}});$("#count").val("50")}else{$("#count > option").each(function(){$(this).attr("disabled",false)})}});if($("#engine").val()=="google"){$("#count > option").each(function(a){if($(this).val()!="50"){$(this).attr("disabled","disabled")}});$("#count").val("50")}ect=new editableClusterTree({debugDivElementId:"clusterEditsMsg",dragAllowed:true,highlightFunction:function(h,n){if(n.length<2){return n}n=n.split(/\s+/);for(var g in h){var a=h[g].split(/\s+/);for(var f in a){if(a[f].length>4){a[f]=stemmer(a[f])}try{var b=new RegExp(RegExp.escape(a[f]),"i");for(var d in n){var c=n[d];if(c.length>0&&c.charAt(0)!="<"&&c.match(b)!=null){n[d]="<b>"+n[d]+"</b>"}}}catch(l){}}}return n.join(" ")}});$(window).unload(function(){ect=null})});function processQ(a){var e=$("#query").val();if(typeof a!="number"){a=1}var c=$("#engine").val();var b=$("#count").val();var d=parseInt(a)+parseInt(b);var f=$("#clusteringAlgo").val();ect.executeSearch(a,e,c,b,d,f);e=null;c=null;b=null;d=null;f=null}function getLatestMostPopularQueries(){$("#clusterQueriesMsg").html("");$("#clusterQueriesMsg").removeClass("error").html('Please wait... <img src="includes/images/loading.gif">');var a=window.location;url=a.protocol+"//"+a.hostname;if(a.port!=80){url+=":"+a.port}url+="/"+a.pathname.substring(1,a.pathname.lastIndexOf("/"));a=null;url+="/rest/getMostPopularQueries";$.ajax({url:url,type:"post",async:true,dataType:"json",success:function(f){if(typeof f=="object"&&f.success==true){var c=f.queries;var b="";if(c.length==0){$("#clusterQueriesMsg").html("No queries have yet been edited.");$("#clusterQueriesMsg").addClass("error")}else{for(var d in c){b+="<a href=\"javascript:invokePopularQuery('"+c[d]+"')\">"+c[d]+"</a><br>"}$("#clusterQueriesMsg").html(b)}c=null;b=null}else{var e="Invalid server response.";if(typeof f.error){e=f.error}$("#clusterQueriesMsg").html(e);$("#clusterQueriesMsg").addClass("error");e=null}},error:function(c,b){$("#clusterQueriesMsg").html("Server connection error.  Please try again.");$("#clusterQueriesMsg").addClass("error")}})}function invokePopularQuery(a){$("#query").val(a);$("#engine").val("google");$("#count").val(50);$("#clusteringAlgo").val(3);processQ(1)}function startTest(b){$("#r1").hide();$("#r2").hide();$("#searchButton").hide();$("#testNext").hide();$("#remainingTagCount").hide();$("#query").hide();$("#queryTest").val("");$("#testMessagesMsg").removeClass("error").html('Please wait while we prepare the test. <img src="includes/images/loading.gif">');$("#testMessages").show();ect.clearTags();ect.clearCurrentSearch();var a=window.location;url=a.protocol+"//"+a.hostname;if(a.port!=80){url+=":"+a.port}url+="/"+a.pathname.substring(1,a.pathname.lastIndexOf("/"));a=null;url+="/rest/test/get/"+b+".json";$.ajax({url:url,type:"post",async:true,dataType:"json",success:function(e){if(typeof e=="object"&&e.success==true){if("logIn" in e){$("#testMessagesMsg").html(e.description);$("#testMessagesMsg").addClass("error")}else{if("done" in e){$("#testMessagesMsg").html(e.description);$("#testMessagesMsg").addClass("success")}else{var f="";if(e.description!=""){f+=" &nbsp; "+e.description}if(e.enableTagging>0){f+=" Please tag "+e.tagCount+" result";if(e.tagCount>1){f+="s"}f+=" before continuing to the next step.";$("#remainingTagCount").html(e.tagCount+" remaining tags.");$("#remainingTagCount").show()}f+="<br><br>";if(e.narrative!=""){f+=" &nbsp; Narrative: "+e.narrative+"<br>"}if(e.enableTagging>0){ect.enableTagging(true)}else{ect.enableTagging(false)}if(e.enableTagging){ect.setTagCount(e.tagCount)}if(e.loggedIn==1){if(e.disableEditting>0){ect.setEditAllowed(false);ect.setDisabledMessage("Editing has been temporarily disabled for this step.")}else{ect.setEditAllowed(true)}}if(e.source!=""){$("#engine").val(e.source)}if(e.results>0){$("#count").val(e.results)}if(e.algorithm<4){$("#clusteringAlgo").val(e.algorithm)}$("#testMessagesMsg").html(f);$("#testNext").show();testExecution=ect.clone(e);testExecution.message=f;if(e.query!=""){$("#query").val(e.query);if(e.query.indexOf("topic:")>-1){var d=jQuery.trim(e.query);d=d.substring(d.indexOf(" ")+1);$("#queryTest").val(d);d=null}else{$("#queryTest").val(e.query)}$("#queryTest").show();if(e.hideCluster>0){$("#cluster").hide()}else{$("#cluster").show()}processQ()}}}}else{var c="Invalid server response.";if(typeof e.error){c=e.error}$("#testMessagesMsg").html(c);$("#testMessagesMsg").addClass("error");c=null}},error:function(d,c){$("#testMessagesMsg").html("Server connection error.  Please try again.");$("#testMessagesMsg").addClass("error")}})}function nextTestStep(){if(testExecution.enableTagging>0){var a=ect.countTags();if(a<testExecution.tagCount){$("#testMessagesMsg").html(testExecution.message+"\n   Please tag all required items before continuing to the next step.");return}if(!sendExecutionInfo(true)){return}}else{if(!sendExecutionInfo(false)){return}}startTest(testExecution.executionId)}function sendExecutionInfo(c){ect.setTagExecutionInfo(true);var d="{tagExecutionInfo:"+$.toJSON(ect.getTagExecutionInfo())+",stepId:"+testExecution.stepId+",times:"+$.toJSON(ect.getExecutionTimes())+",cluster:"+ect.getInternalStructure()+"}";var a=window.location;url=a.protocol+"//"+a.hostname;if(a.port!=80){url+=":"+a.port}url+="/"+a.pathname.substring(1,a.pathname.lastIndexOf("/"));a=null;url+="/rest/test/put/"+testExecution.executionId+".json";var b=false;$.ajax({url:url,type:"POST",contentType:"multipart/form-data",async:false,data:d,processData:false,dataType:"json",success:function(e){if(typeof e=="object"&&typeof e.error!="undefined"){$("#testMessagesMsg").html(e.error);$("#testMessagesMsg").addClass("error");b=false;e=null;return}e=null;b=true},error:function(f,e){$("#testMessagesMsg").html("Server connection error.  Please try again.");$("#testMessagesMsg").addClass("error");b=false}});return b}function onEnter(field,methodCall){$("#"+field).keyup(function(e){if(e.keyCode==13){try{eval(methodCall+"()")}catch(exception){}}})}function hovImgOver(a){var c,b;if(typeof a.src=="undefined"){c=this.src;b=c.substr(c.lastIndexOf("."));if(c.indexOf("_hov")==-1){this.src=c.replace(b,"_hov"+b)}}else{c=a.src;b=c.substr(c.lastIndexOf("."));if(c.indexOf("_hov")==-1){a.src=c.replace(b,"_hov"+b)}}c=null;b=null}function hovImgOut(a){var b;if(typeof a.src=="undefined"){b=this.src;this.src=b.replace("_hov","")}else{b=a.src;a.src=b.replace("_hov","")}b=null};