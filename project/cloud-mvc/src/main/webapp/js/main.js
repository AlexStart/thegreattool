$(document).ready(function() {
	onReady();
});

function onReady() {
	var removeFooter = (110 + $('#body').height()) > window.innerHeight;

	if (removeFooter) {
		$("footer").removeClass("footer");
	}

}
