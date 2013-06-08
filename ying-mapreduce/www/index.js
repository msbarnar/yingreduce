var nodeInfoReq;
	
$(document).ready(initialize());

function initialize() {
	// Update node info every 3 seconds
	setInterval(updateNodeInfo, 3000);
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

function updateNodeInfo() {
	nodeInfoReq = makeRequest("node", displayNodeInfo);
}

// Show node info in pane
function displayNodeInfo() {
	if (checkConnectivity(nodeInfoReq)) {
			var message = nodeInfoReq.responseXML.getElementsByTagName("status")[0];
			document.getElementById("nodeStatus").innerHTML = message.childNodes[0].nodeValue;
	} else {
		document.getElementById("nodeStatus").innerHTML = "Unknown";
	}
}