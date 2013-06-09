var network_updateTimer;
var network_active = false;
var network_isConnected = false;

function network_start() {
	$("#tab-network").removeClass("nodisplay");
	$("#tab-btn-network").addClass("active");
	network_updateTimer = setInterval(update_network, 3000);
	network_active = true;
}
function network_stop() {
	network_active = false;
	clearInterval(network_updateTimer);
	$("#tab-btn-network").removeClass("active");
	$("#tab-network").addClass("nodisplay");
}

function update_network() { 
}

function network_connect() {
	var ipaddr = $("input#connect-addr").val();
	$.ajax({
		url: "http://localhost:8777/tables", 
		data: {method: "connect", uri: ipaddr}
	}).done(displayConnectMessage)
		.fail(displayConnectError); 
}

function displayConnectMessage(data) {	
	var exception = data.getElementsByTagName("exception")[0];
	if (exception) {
		displayConnectError(data);
		return;
	} else {
		resBox = $("li#connect-result");
		resBox.addClass("success");
		var remoteAddr = data.getElementsByTagName("remote-addr")[0];
		if (remoteAddr) {
			remoteAddr = "<br>Remote node: " + remoteAddr.childNodes[0].nodeValue;
		} else {
			remoteAddr = "";
		}
		resBox.html("<span class=\"subheading\">Successfully connected</span>" + remoteAddr);
		$("div#connect-result").removeClass("nodisplay");
		$("div#connect-instructions").addClass("nodisplay");
		
		network_isConnected = true;
	}
}
function displayConnectError(data) {
	resBox = $("li#connect-result"); 
	
	resBox.addClass("error");
	var exception = data.getElementsByTagName("exception")[0].childNodes[0].nodeValue;
	if (exception) {
		var detail = data.getElementsByTagName("detail")[0].childNodes[0].nodeValue;
		if (detail != "null") {
			detail + "<br>" + detail;
		} else {
			detail = "";
		}
		var cause = data.getElementsByTagName("cause")[0].childNodes[0].nodeValue;
		if (cause && cause != "null") {
			cause = "<br>Caused by:<br>" + cause;
		} else {
			cause = "";
		}
		resBox.html("<span class=\"exception\">" + exception + "</span>"
			+ detail + cause);
	}
	$("div#connect-result").removeClass("nodisplay");
	network_isConnected = false;
}

function getNodeStatus(data) {
	if (!network_active) {
		return;
	}
	var exception = data.getElementsByTagName("exception")[0];
	if (exception) {
		displayNodeError(exception.childNodes[0].nodeValue);
		return;
	}
	// Bug 00000001
	var receiveSink = data.getElementsByTagName("KadReceiveTransportSink")[0];
	if (!receiveSink) {
		malformedResponse(data);
		return;
	}
	var localEndpoint = receiveSink.getElementsByTagName("local-endpoint")[0].childNodes[0].nodeValue;
	if (!localEndpoint) {
		displayNodeDown();
	} else {
		displayNodeRunning(localEndpoint);
	}
}

function displayNodeRunning(localEndpoint) {
	$("li#node-running").html("<span class=\"subheading\">Node is running</span><br>" + localEndpoint);
	$("li#node-running").removeClass("nodisplay");
	
	$("li#node-down").addClass("nodisplay");
	$("li#node-error").addClass("nodisplay");
	$("li#node-error-message").addClass("nodisplay");
}
function displayNodeDown() {
	var nodeDownElement = $("li#node-down");
	nodeDownElement.html("Node is not currently listening for connections.<br>If you think you are receiving this message in error, please<br>" + makeBugReportLink());
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
	$("li#node-error-message").html(errMessage);
}

function malformedResponse(data) {
	displayNodeError("Response from node was malformed. This is probably a bug in the user interface."
			+ "<br>" + makeBugReportLink(00000001, xml2string(data)));
}