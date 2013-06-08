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
		$("div#connectionStatus").css("background-color", "#E6FFB2");
		return true;
	} else {
		$("span#connectionStatus").text("not connected");
		$("div#connectionStatus").css("background-color", "#FFE6E6");
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