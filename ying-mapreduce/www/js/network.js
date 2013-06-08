var network_updateTimer;

$(document).ready(init_network());

function network_start() {
	$("#tab-network").removeClass("nodisplay");
	$("#tab-btn-network").addClass("active");
	network_updateTimer = setInterval(update_network, 3000);
}
function network_stop() {
	clearInterval(network_updateTimer);
	$("#tab-btn-network").removeClass("active");
	$("#tab-network").addClass("nodisplay");
}

function update_network() {
	
}