function settings_start() {
	$("#tab-settings").removeClass("nodisplay");
	$("#tab-btn-settings").addClass("active");
}
function settings_stop() {
	$("#tab-btn-settings").removeClass("active");
	$("#tab-settings").addClass("nodisplay");
}