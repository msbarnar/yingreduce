function tables_start() {
	$("#tab-tables").removeClass("nodisplay");
	$("#tab-btn-tables").addClass("active");
}
function tables_stop() {
	$("#tab-btn-tables").removeClass("active");
	$("#tab-tables").addClass("nodisplay");
}