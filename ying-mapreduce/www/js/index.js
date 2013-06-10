var nodeInfoReq;
	
$(document).ready(initialize());

function initialize() {
	// Update node info every 3 seconds
	setInterval(checkConnectivity, 3000);
	checkConnectivity();
}

function makeRequest(url, doneCallback, failCallback) {
	return $.ajax("/" + url).done(doneCallback).fail(failCallback);
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
	// Display the status on the network pane
	getNodeStatus(data);
}
function displayDisconnected() {
	$("span#connectionStatus").text("not connected");
	$("img#connection-off").removeClass("nodisplay");
	$("img#connection-on").addClass("nodisplay");
	$("div.section-not-connected").removeClass("nodisplay");
	$("div.section-body").addClass("nodisplay");
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

function xml2string(node) {
	if (typeof(XMLSerializer) !== 'undefined') {
		var serializer = new XMLSerializer();
		return serializer.serializeToString(node);
	} else if (node.xml) {
		return node.xml;
	}
}

function makeBugReportLink(bugNo, detail) {
	return "<a class=\"inline bugreport\" "
		+ "onClick='sendBugReport(this, " + bugNo + ");' "
		+ "href=\"mailto:msbarnar@gmail.com?subject=Mapreduce Bug&Body=%0D%0A%0D%0A%0D%0A(DETAILS FOR BUG " + bugNo + ")%0D%0A" + detail
		+ "\">Report this bug to the developers</a>";
}

function sendBugReport(element, bugNo) {
	element.innerText = "Bug report sent";
}