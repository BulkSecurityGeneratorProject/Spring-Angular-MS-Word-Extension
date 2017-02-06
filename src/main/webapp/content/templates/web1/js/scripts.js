var mobileNavController;
$(document).ready(function () {


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

    var navLinkNodes = leftNav.find('a');
    var viewSwitchers = leftNav.find('a[data-viewid]');

    var mobileNav = $('.mobile-nav nav'); //nav wrapper

    var mobileToggleNav = mobileNav.find('a'); //alle 1e level nav items

    var viewWrapper = $('#right-side'); //view wrapper

    var views = viewWrapper.find('.view'); //alle views

    var firstView;

    //start view bepalen en actief zetten
    if (leftNav.find('> ul > li').first().has(".sub-nav").length != 0) {
        /*eerste item heeft sub navigatie*/
        firstView = viewSwitchers.first().data('viewid'); //id eerste item

    } else {
        /*eerste item heeft geen sub navigatie*/
        firstView = navLinkNodes.first().data('viewid'); //id eerste item
    }

    //view die bij eerste item hoort zichtbaar maken
    switchView(views, firstView);


    $('nav a.link').click(function (e) {
        e.preventDefault();

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

        switchView(views, aNode.data('viewid'));
    });


    $('nav a.menu-toggle').click(function (e) {
        e.preventDefault();
        e.stopPropagation();

        var aNode = $(this);

        toggleMenuTree(aNode);
    });

    function openMenuTree(aNode){
        var liNode = aNode.parent();
        liNode.children(".sub-nav").stop().slideDown(300).addClass('open');

        refreshPlusMinusIcons();
    }

    function toggleMenuTree(aNode){
        var liNode = aNode.parent();
        liNode.children(".sub-nav").stop().slideToggle(300).toggleClass('open');

        refreshPlusMinusIcons();
    }

    $('a[data-viewid]').click(function(e){
        var viewID = $(this).data('viewid');

        // Check if this content exists
        var contentToShow = $('section[data-viewid="'+viewID+'"]');

        if(contentToShow.length == 0){
            // Try to find the content linked to this GUID
            var guid = viewID;
            var menuLink = $('a[data-guid="'+guid+'"]:first');

            if(menuLink.length > 0){
                var newViewId = menuLink.data('viewid');
                if(newViewId){
                    viewID = newViewId;
                }
            }
        }

        switchView(views, viewID);
    });



//SWITCH BETWEEN CONTENT VIEWS =====================================================
    var contentNav = $('.view .view-content'); //nav wrapper

    var contentViewSwitchers = contentNav.find('.part'); //nav items

    var anchorViewSwitchers = contentNav.find('.anchor'); //alle anchor links

    var contentViewWrapper = $('#right-side .view'); //content view wrapper

    var contentViews = contentViewWrapper.find('.view-content'); //alle content views

    var backButtons = contentViews.find('.back'); //alle back buttons

    var contentSlider;

    var URLhash;


    //click-event navigatie items
    contentViewSwitchers.on('click', function (e) {
        switchView(contentViews, $(this).data('viewid'));
        contentSlider = $('.view .view-content.active .view-content-slider.owl-carousel');
        URLhash = $(this).index();
        contentSlider.owlCarousel({
            items: 1,
            nav: (contentSlider.find('.slider-item').length > 1),
            navText: ["<i class='icon-left'></i>", "<i class='icon-right'></i>"],
            dots: false,
            loop: false,
            URLhashListener: true,
            startPosition: URLhash,
            mouseDrag: false,
            pullDrag: false,
            navSpeed: 300,
            navRewind: false
        });

    });
    //click-event anchor links
    anchorViewSwitchers.on('click', function (e) {
        switchAnchorView(contentViews, $(this).data('viewid'));
        contentSlider = $('.view .view-content.active .view-content-slider.owl-carousel');
        URLhash = $(this).index();
        contentSlider.owlCarousel({
            items: 1,
            nav: (contentSlider.find('.slider-item').length > 1),
            navText: ["<i class='icon-left'></i>", "<i class='icon-right'></i>"],
            dots: false,
            loop: false,
            URLhashListener: true,
            startPosition: URLhash,
            mouseDrag: false,
            pullDrag: false,
            navSpeed: 300,
            navRewind: false
        });

    });
    //click-event back buttons
    backButtons.on('click', function (e) {
        e.preventDefault();
        switchView(contentViews, $(this).data('viewid'));
    });


});

$(window).on('load resize', function () {
    //mobile menu
    if (window.innerWidth < 960) {
        $("body .hamburger.desktop").hide();
        $("body .hamburger.mobile").show();
        $('body').removeClass('desktop-nav-closed');
    }
    else {
        $("body .hamburger.mobile").hide();
        $("body .hamburger.desktop").show();
        if (mobileNavController.getActiveSlidebar()) {
            mobileNavController.close();
        }
    }
});

function refreshPlusMinusIcons(){
    $('.sub-nav').each(function(i, node){
        node = $(node);

        var parentLi = node.closest('li');
        var icon = parentLi.children('a.menu-toggle').find('i');

        if(node.hasClass('open')){
            // Show minus
            icon.removeClass('icon-plus').addClass('icon-minus');
        }else{
            // Show plus
            icon.removeClass('icon-minus').addClass('icon-plus');
        }
    });
}

function switchView(views, viewID) {
    console.log('switchView, viewID = '+viewID);

    // Close mobile menu
    if (mobileNavController.getActiveSlidebar()) {
        // Close mobile menu if open
        mobileNavController.close();
    }

    // Update active class on menu
    if(viewID){
        var guidParts = viewID.split('-');
        var guid = guidParts[0];

        $('nav a').removeClass('active');
        var activeLink = $('nav a[data-viewid="'+guid+'"]');
        activeLink.addClass('active');

        // Open all levels above the current
        activeLink.parents('.sub-nav').addClass('open').attr('style', '');

        // Show section
        $('section[data-viewid="'+guid+'"]').show();
    }

    // Fix the plus/minus icons of all parent levels
    refreshPlusMinusIcons();

    views.hide().removeClass('active');

    views.each(function () {
        var t = $(this);
        if (t.data('viewid') === viewID) {
            t.show().addClass('active');

            if (t.has(".view-content").length != 0) {
                //view => plaats eerste view-content zichtbaar
                t.find('.view-content').hide().removeClass('active');

                var firstViewContent = t.find('.view-content:first');
                firstViewContent.show();
                firstViewContent.addClass('active');
            }
            return false;
        } else {
            if (t.is(':visible')) {
                t.hide().removeClass('active');
            }
        }
    });
}

function switchAnchorView(views, viewID) {
    console.log('switchAnchorView, viewID = '+viewID);

    views.hide().removeClass('active');

    views.each(function () {
        if ($(this).data('viewid') === viewID) {
            if (!$(this).parent().hasClass('active')) {
                views.not($(this)).parent().hide().removeClass('active');
                $(this).parent().show().addClass('active');
            }
            $(this).show().addClass('active');
            updateLeftNav(views,viewID)
            return false;
        } else {
            if ($(this).is(':visible')) {
                $(this).hide().removeClass('active');
            }
        }
    });
}

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
                /*sub navigatie*/
                $(this).addClass('active');
                $(this).parent().parent().parent().find('> a').addClass('active');
                $(this).parent().parent().stop().slideDown(0).addClass('open');
            } else {
                /*geen sub navigatie*/
                $(this).addClass('active');
            }
            return false;
        }
    });

    refreshPlusMinusIcons();


}
