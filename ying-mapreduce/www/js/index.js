var nodeInfoReq;
	
$(document).ready(initialize());

function initialize() {
	// Update node info every 3 seconds
	setInterval(function() {
		nodeInfoReq = makeRequest("node", updateConnectionStatus);
	}, 3000);
}

function getXmlHttpRequest() {
	if (typeof XMLHttpRequest != "undefined") {
		return new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
}

function makeRequest(url, callback) {
	var req = getXmlHttpRequest();
	req.open("GET", "http://localhost:8777/" + url, true);
	req.onreadystatechange = callback;
	req.send(null);
	return req;
}

// Show in status bar if we're connected to the local node
function checkConnectivity(req) {
	if (req.readyState == 4 && req.status == 200) {
		$("span#connectionStatus").text("connected");
		$("img#connection-off").addClass("nodisplay");
		$("img#connection-on").removeClass("nodisplay");
		$("div.section-not-connected").addClass("nodisplay");
		$("div.section-body").removeClass("nodisplay");
		return true;
	} else {
		$("span#connectionStatus").text("not connected");
		$("img#connection-off").removeClass("nodisplay");
		$("img#connection-on").addClass("nodisplay");
		$("div.section-not-connected").removeClass("nodisplay");
		$("div.section-body").addClass("nodisplay");
		return false;
	}
}

function updateConnectionStatus() {
	checkConnectivity(nodeInfoReq);
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