var table_updateTimer;

function tables_start() {
	update_tables();
	$("#tab-tables").removeClass("nodisplay");
	$("#tab-btn-tables").addClass("active");
	table_updateTimer = setInterval(update_tables, 3000);
}
function tables_stop() {
	clearInterval(table_updateTimer);
	$("#tab-btn-tables").removeClass("active");
	$("#tab-tables").addClass("nodisplay");
}

function startTableUpload() {
	$("#upload-target").load(stopTableUpload);
	return true;
}

function stopTableUpload() {
	var data = $("#upload-target").contents();
	var success = data.find("success").text();
	if (!success) {
		success = "false";
	}
	if (success == "true") {
		$("#upload-successful").removeClass("nodisplay");
	} else {
		$("#upload-error").removeClass("nodisplay");
	}
	$("#upload-result").removeClass("nodisplay");
	
	updateTableList();
	return true;
}

function update_tables() {
	updateTableList();
}

function updateTableList() {
	$.ajax({
		url: "/tables",
		data: {method: "tables"}
	}).done(displayTableList)
	.fail(displayTableListError);
}

function displayTableList(data) {
	var new_tbody = document.createElement("tbody");
	new_tbody.setAttribute("id", "table-store");
	
	var $tables = $(data).find("table");
	$.each($tables, function(index, value) {
		var $tableid = $(value).find("id").text();
		var $pages = $(value).find("pageCount").text();
		$(new_tbody).append('<tr><td class="first"><a href="/tables?method=get&tableid='+$tableid+'" class="table-selector">'
				+ $tableid + '</a></td>'
				+ '<td>' + $pages + '</td></tr>');
	});
	
	var tbody = $("tbody#table-store");
	tbody.replaceWith(new_tbody);
}

function displayTableListError(data) {
	$("#table-list-update-error").removeClass("nodisplay");
}