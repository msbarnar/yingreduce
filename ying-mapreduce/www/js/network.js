var network_updateTimer;
var network_active = false;

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
	var localEndpoint = "";//receiveSink.getElementsByTagName("local-endpoint")[0].childNodes[0].nodeValue;
	if (!localEndpoint) {
		displayNodeDown();
	} else {
		displayNodeRunning(localEndpoint);
	}
}

function displayNodeRunning(localEndpoint) {
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