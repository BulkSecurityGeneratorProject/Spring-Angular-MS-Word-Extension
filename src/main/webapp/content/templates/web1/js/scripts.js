var mobileNavController;
$(document).ready(function () {


    $(window).on('hashchange', function (e) {
        var oldUrl = e.originalEvent.oldURL;
        var newUrl = e.originalEvent.newURL;

        var oldHash = oldUrl.substring(oldUrl.indexOf('#'));
        var newHash = window.location.hash;

        // Remove prefix #
        if (oldHash) {
            oldHash = oldHash.substring(1);
        }
        if (newHash) {
            newHash = newHash.substring(1);
        }

        console.log('Detected a hash change from "' + oldHash + '" to "' + newHash + '"');

        if (newHash) {
            if (isSlideOnCurrentSlider(oldHash, newHash)) {
                // Let OwlCarousel slide on it's own to the new slide in the same slider...

            } else {
                // Hard jump to the new "page"
                switchView(views, newHash);
            }
        }
    });

    function isSlideOnCurrentSlider(oldHash, newHash) {
        var currentSlide = $('[data-hash="' + oldHash + '"]');
        if (currentSlide.length > 0) {
            var slider = currentSlide.closest('.owl-stage');
            if (slider.length > 0) {
                var targetSlide = slider.find('[data-hash="' + newHash + '"]');
                if (targetSlide.length > 0) {
                    return true;
                }
            }
        }

        return false;
    }


//LEFT SIDE SCROLLBAR ========================================
    /*De left side is fixed, dus als ze hoger wordt dan het scherm wordt een scrollbar getoond,
     perffectScrollbar zorgt voor visueel mooiere scrollbar ipv standaard scrollbar*/
    var leftSide = $('#left-side');
    leftSide.perfectScrollbar();

//DESKTOP MENU TOGGLE ========================================
    // Toggle mobile nav (Slidebars)
    var btnToggleDesktopNav = $('body').find('.hamburger.desktop');

    btnToggleDesktopNav.on('click', function (e) {

        e.preventDefault(); //stopt standaard gedrag van de button
        e.stopPropagation(); //stopt bubbling

        $('body').toggleClass('desktop-nav-closed');

    });

//MOBILE MENU OVERLAY ========================================
    mobileNavController = new slidebars();
    mobileNavController.init();
    // Add close class to canvas container when Slidebar is opened
    $(mobileNavController.events).on('opening', function (e) {
        $('#right-side').addClass('js-close-any');
    });
    // Remove close class from canvas container when Slidebar is closed
    $(mobileNavController.events).on('closing', function (e) {
        $('#right-side').removeClass('js-close-any');
    });
    // Close any
    $(document).on('click', '.js-close-any', function (e) {
        if (mobileNavController.getActiveSlidebar()) {
            e.preventDefault();
            e.stopPropagation();
            mobileNavController.close();
        }
    });
    // Close via close-buttons
    var closeBtns = $('body .mobile-nav .close-mobile-nav');
    closeBtns.on('click', function (e) {
        if (mobileNavController.getActiveSlidebar()) {
            e.preventDefault();
            e.stopPropagation();
            mobileNavController.close();
        }
    });
    // Toggle mobile nav (Slidebars)
    var btnOpenMobileNav = $('body').find('.hamburger.mobile');

    btnOpenMobileNav.on('click', function (e) {

        e.preventDefault(); //stopt standaard gedrag van de button
        e.stopPropagation(); //stopt bubbling

        mobileNavController.toggle('mobile-nav');

    });

//LEFT NAV TOGGLE OR SWITCH BETWEEN VIEWS =====================================================
    var leftNav = $('#left-side .left-nav nav'); //nav wrapper

    var navLinkNodes = leftNav.find('a[href^=#]');

    var mobileNav = $('.mobile-nav nav'); //nav wrapper

    var viewWrapper = $('#right-side'); //view wrapper

    var views = viewWrapper.find('.view'); //alle views

    var firstMenuItem;

    //start view bepalen en actief zetten
    if (leftNav.find('> ul > li').first().has(".sub-nav").length != 0) {
        /*eerste item heeft sub navigatie*/
        firstMenuItem = navLinkNodes.first(); //id eerste item

    } else {
        /*eerste item heeft geen sub navigatie*/
        firstMenuItem = navLinkNodes.first(); //id eerste item
    }

    var currentHash = window.location.hash;
    if (currentHash) {
        currentHash = currentHash.substring(1);
    }

    if (currentHash) {
        // Active the current hash
        switchView(views, currentHash);

    } else {
        // Show first menu item by default
        var firstPageHref = firstMenuItem.attr('href');
        window.location.href = firstPageHref;
    }

    //switchView(views, firstView);


    $('nav a.link').click(function (e) {
        var aNode = $(this);
        var liNode = aNode.parent();

        if (liNode.has(".sub-nav").length != 0) {
            /*heeft sub navigatie*/

            openMenuTree(aNode);

        } else {
            /*heeft geen sub navigatie*/
            leftNav.removeClass('active');
            aNode.addClass('active');
        }
    });


    $('nav a.menu-toggle').click(function (e) {
        e.preventDefault();
        e.stopPropagation();

        var aNode = $(this);

        toggleMenuTree(aNode);
    });

    /*
        $('a[data-viewid]').click(function(e){
            var viewID = null;
            var linkToViewId = $(this).data('viewid');

            // Check if this content exists
            var contentToShow = $('section[data-viewid="'+linkToViewId+'"]');

            if(contentToShow.length == 0){
                // Try to find the content linked to this GUID
                // var menuLink = $('a[data-guid="'+linkToViewId+'"]:first');
                //
                // if(menuLink.length > 0){
                //     var newViewId = menuLink.data('viewid');
                //     if(newViewId){
                //         viewID = newViewId;
                //     }
                // }

                viewID = linkToViewId;

            }else{
                viewID = contentToShow.data('viewid');
            }

            if(viewID){
                switchView(views, viewID);
            }
        });
    */

    function openMenuTree(aNode) {
        var liNode = aNode.parent();
        liNode.children(".sub-nav").stop().slideDown(300).addClass('open');

        refreshPlusMinusIcons();
    }

    function toggleMenuTree(aNode) {
        var liNode = aNode.parent();
        liNode.children(".sub-nav").stop().slideToggle(300).toggleClass('open');

        refreshPlusMinusIcons();
    }

});

$(window).on('load throttledresize', function () {
    //mobile menu
    if (window.innerWidth < 960) {
        $('body').removeClass('desktop-nav-closed');
    }
    else {
        if (mobileNavController.getActiveSlidebar()) {
            mobileNavController.close();
        }
    }
});

function refreshPlusMinusIcons() {
    $('.sub-nav').each(function (i, node) {
        node = $(node);

        var parentLi = node.closest('li');
        var icon = parentLi.children('a.menu-toggle').find('i');

        if (node.hasClass('open')) {
            // Show minus
            icon.removeClass('icon-plus').addClass('icon-minus');
        } else {
            // Show plus
            icon.removeClass('icon-minus').addClass('icon-plus');
        }
    });
}

function switchView(mapNodes, viewID) {
    console.log('switchView to viewID ' + viewID);

    // Close mobile menu
    if (mobileNavController.getActiveSlidebar()) {
        // Close mobile menu if open
        mobileNavController.close();
    }

    // Update active class on menu
    if (viewID) {
        var guidParts = viewID.split('-');
        var guid = guidParts[0];

        $('nav a').removeClass('active');
        var activeLink = $('nav a[data-viewid="' + guid + '"]');
        activeLink.addClass('active');

        // Open all levels above the current
        activeLink.parents('.sub-nav').addClass('open').attr('style', '');

        // Show section
        $('section[data-viewid="' + guid + '"]').show();
    }

    // Fix the plus/minus icons of all parent levels
    refreshPlusMinusIcons();

    mapNodes.hide().removeClass('active');

    mapNodes.each(function () {
        var t = $(this);
        // Find the right view to show

        var isLinkToMap = t.data('viewid') === viewID;

        var slideNode = t.find('.view-content-with-slider [data-hash="' + viewID + '"]');
        var isLinkToBlockInsideMap = slideNode.length > 0;

        if (isLinkToMap || isLinkToBlockInsideMap) {

            t.show().addClass('active');

            // Hide al previously visible stuff...
            t.find('.view-content').hide().removeClass('active');


            if (isLinkToMap && t.has(".view-content").length != 0) {
                // Show the first view-content if there is such an element...
                var firstViewContent = t.find('.view-content:first');
                firstViewContent.show();
                firstViewContent.addClass('active');


            } else if (isLinkToBlockInsideMap) {
                // Show the slider
                var nodeWithSlider = t.find('.view-content-with-slider');
                nodeWithSlider.show().addClass('active');
            }

            return false;

        } else {

            if (t.is(':visible')) {
                // Hide all...
                t.hide().removeClass('active');
            }
        }
    });


    // Update slider...
    var slideToShow = $('[data-hash="' + viewID + '"]');
    if (slideToShow.length > 0) {
        var sliderContainer = slideToShow.closest('.view-content');
        var slider = sliderContainer.find('.owl-carousel');

        var showNav = (slider.find('.slider-item').length > 1);

        slider.owlCarousel({
            items: 1,
            nav: showNav,
            navText: ["<i class='icon-left'></i>", "<i class='icon-right'></i>"],
            dots: false,
            loop: false,
            URLhashListener: true,
            startPosition: 'URLHash',
            mouseDrag: false,
            pullDrag: false,
            navSpeed: 300,
            navRewind: false
        });

    }

}

// function switchAnchorView(views, viewID) {
//     console.log('switchAnchorView, viewID = ' + viewID);
//
//     views.hide().removeClass('active');
//
//     views.each(function () {
//         if ($(this).data('viewid') === viewID) {
//             if (!$(this).parent().hasClass('active')) {
//                 views.not($(this)).parent().hide().removeClass('active');
//                 $(this).parent().show().addClass('active');
//             }
//             $(this).show().addClass('active');
//             updateLeftNav(views, viewID)
//             return false;
//         } else {
//             if ($(this).is(':visible')) {
//                 $(this).hide().removeClass('active');
//             }
//         }
//     });
// }

function updateLeftNav(views, viewContentID) {
    var leftNav = $('#left-side .left-nav nav'); //nav wrapper
    var mobileNav = $('.mobile-nav nav'); //nav wrapper

    var leftToggleNav = leftNav.find('>ul > li > a'); //alle 1e level nav items
    var leftSubNav = leftNav.find('.sub-nav > li > a'); //alle 2e level nav items

    var mobileToggleNav = mobileNav.find('>ul > li > a'); //alle 1e level nav items
    var mobileSubNav = mobileNav.find('.sub-nav > li > a'); //alle 2e level nav items

    var viewID;

    views.each(function () {
        if ($(this).data('viewid') === viewContentID) {
            viewID = $(this).parent().data('viewid');
            return false;
        }
    });


    leftToggleNav.removeClass('active');
    leftSubNav.removeClass('active');


    mobileToggleNav.removeClass('active');
    mobileSubNav.removeClass('active');

    $(leftNav.find('a')).each(function () {
        if ($(this).data('viewid') === viewID) {
            if ($(this).parent().parent().hasClass("sub-nav")) {
                /* wel sub navigatie */
                $(this).addClass('active');
                $(this).parent().parent().parent().find('> a').addClass('active');
                $(this).parent().parent().stop().slideDown(0).addClass('open');
            } else {
                /* geen sub navigatie */
                $(this).addClass('active');
            }
            return false;
        }
    });

    refreshPlusMinusIcons();


}
