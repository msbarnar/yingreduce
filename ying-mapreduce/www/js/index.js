var nodeInfoReq;
	
$(document).ready(initialize());

function initialize() {
	// Update node info every 3 seconds
	setInterval(checkConnectivity, 3000);
	checkConnectivity();
}

function getXmlHttpRequest() {
	if (typeof XMLHttpRequest != "undefined") {
		return new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
}

function makeRequest(url, doneCallback, failCallback) {
	return $.ajax("http://localhost:8777/" + url).done(doneCallback).fail(failCallback);
}

// Show in status bar if we're connected to the local node
function checkConnectivity() {
	makeRequest("node", displayConnected, displayDisconnected);
}

function displayConnected(data) {
	$("span#connectionStatus").text("connected");
	$("img#connection-off").addClass("nodisplay");
	$("img#connection-on").removeClass("nodisplay");
	$("div.section-not-connected").addClass("nodisplay");
	$("div.section-body").removeClass("nodisplay");
	getNodeStatus(data);
}
function displayDisconnected() {
	$("span#connectionStatus").text("not connected");
	$("img#connection-off").removeClass("nodisplay");
	$("img#connection-on").addClass("nodisplay");
	$("div.section-not-connected").removeClass("nodisplay");
	$("div.section-body").addClass("nodisplay");
}

function getNodeStatus(data) {
	var nodeStatus = data.getElementsByTagName("status")[0].childNodes[0].nodeValue;
	if (nodeStatus.toLowerCase() == "running") {
		displayNodeRunning();
	} else if (nodeStatus.toLowerCase() == "down") {
		displayNodeDown();
	} else if (nodeStatus.toLowerCase() == "error") {
		displayNodeError(data.getElementsByTagName("message")[0].childNodes[0].nodeValue);
	} else {
		displayNodeError("Unknown status: " + nodeStatus);
	}
}

function displayNodeRunning() {
	$("li#node-running").removeClass("nodisplay");
	$("li#node-down").addClass("nodisplay");
	$("li#node-error").addClass("nodisplay");
	$("li#node-error-message").addClass("nodisplay");
}
function displayNodeDown() {
	$("li#node-running").addClass("nodisplay");
	$("li#node-down").removeClass("nodisplay");
	$("li#node-error").addClass("nodisplay");
	$("li#node-error-message").addClass("nodisplay");
}
function displayNodeError(errMessage) {
	$("li#node-running").addClass("nodisplay");
	$("li#node-down").addClass("nodisplay");
	$("li#node-error").removeClass("nodisplay");
	$("li#node-error-message").removeClass("nodisplay");
	$("li#node-error-message").text(errMessage);
}

function switch_tab_network() {
	tables_stop();
	mapreduce_stop();
	settings_stop();
	network_start();
}
function switch_tab_tables() {
	network_stop();
	mapreduce_stop();
	settings_stop();
	tables_start();
}
function switch_tab_mapreduce() {
	network_stop();
	tables_stop();
	settings_stop();
	mapreduce_start();
}
function switch_tab_settings() {
	network_stop();
	tables_stop();
	mapreduce_stop();
	settings_start();
}