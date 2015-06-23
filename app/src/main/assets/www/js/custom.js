
// SWIPE PANEL
$( document ).on( "pagecreate", "#demo-page", function() {
    $( document ).on( "swipeleft swiperight", "#demo-page", function( e ) {
        // We check if there is no open panel on the page because otherwise
        // a swipe to close the left panel would also open the right panel (and v.v.).
        // We do this by checking the data that the framework stores on the page element (panel: open).
        if ( $( ".ui-page-active" ).jqmData( "panel" ) !== "open" ) {
            if ( e.type === "swipeleft" ) {
                $( "#right-panel" ).panel( "open" );
            } else if ( e.type === "swiperight" ) {
                $( "#left-panel" ).panel( "open" );
            }
        }
    });
});

// LOADING
$( document ).on( "click", ".show-page-loading-msg", function() {
    var $this = $( this ),
        theme = $this.jqmData( "theme" ) || $.mobile.loader.prototype.options.theme,
        msgText = $this.jqmData( "msgtext" ) || $.mobile.loader.prototype.options.text,
        textVisible = $this.jqmData( "textvisible" ) || $.mobile.loader.prototype.options.textVisible,
        textonly = !!$this.jqmData( "textonly" );
        html = $this.jqmData( "html" ) || "";
    $.mobile.loading( "show", {
            text: msgText,
            textVisible: textVisible,
            theme: theme,
            textonly: textonly,
            html: html
    });
})
.on( "click", ".hide-page-loading-msg", function() {
    $.mobile.loading( "hide" );
});

// SPIN
$('.fa-spinner').hover(function() {
    $(this).addClass('fa-spin');
});

// Authentication
var testHarnessDomain = "http://content-poc-api.cambridgelms.org";

$(document).ready(function() {
    $('#submit-5').click(function() {
        $.post(testHarnessDomain + "/v1.0/authorize",
        "grant_type=password&client_id=app&username=" + $("#username").val() + "&password=" + $("#password").val(),
        function(json) {
            if (json.error_message) {
                // Inform the user to confirm his / her credentials
                alert(json.error_message);
                console.error(json);
            } else  {
                window.location = "#learning";
                // Call native function here
                window.JSInterface.saveAuthenticationLogin(JSON.stringify(json), $("#username").val(), $("#password").val());
                console.info(json);
            }
        }, "json");;
    });
});


function showVideo() {
    window.JSInterface.showVideo();
}

<<<<<<< HEAD
// SPIN
$('.fa-spinner').hover(function() {
    $(this).addClass('fa-spin');
});

=======
function hideVideo() {
    window.JSInterface.hideVideo();
}
>>>>>>> origin/master
