(function(w, $){
    var UrlHashMonitor = {};
    UrlHashMonitor.oldHash = '';
    UrlHashMonitor.newHash = '';
    UrlHashMonitor.oldHref = '';
    UrlHashMonitor.newHref = '';
    UrlHashMonitor.onHashChange = function(f){
        $(window).on('hashchange', function(e){
            UrlHashMonitor.oldHash = UrlHashMonitor.newHash;
            UrlHashMonitor.newHash = w.location.hash;
            UrlHashMonitor.oldHref = UrlHashMonitor.newHref;
            UrlHashMonitor.newHref = w.location.href;
            f(e);
        });
    };
    UrlHashMonitor.init = function(){
        UrlHashMonitor.oldHash = UrlHashMonitor.newHash =   w.location.hash;
        UrlHashMonitor.oldHref = UrlHashMonitor.newHref = w.location.href;
    };
    w.UrlHashMonitor = UrlHashMonitor;
    return UrlHashMonitor;

})(window, window.jQuery);

