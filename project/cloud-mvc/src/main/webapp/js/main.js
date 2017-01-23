$(document).ready(function() {
	onReady();
});

function onReady() {
	var removeFooter = (110 + $('#body').height()) > window.innerHeight;

	if (removeFooter) {
		$("footer").removeClass("footer");
	}

	$(".update-button").attr("disabled", true);
		
	$(".dropdown-menu").on("click", "li", function(event){
        $(".dropdown-label").text(event.target.text);
        $(".update-button").attr("disabled", false);
    })
}
