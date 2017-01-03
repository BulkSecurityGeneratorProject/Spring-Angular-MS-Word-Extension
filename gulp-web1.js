var gulp = require('gulp');

var less = require('gulp-less');
var sourcemaps = require('gulp-sourcemaps');
var minifycss = require('gulp-minify-css');
var autoprefixer = require('gulp-autoprefixer');
var clean = require('gulp-clean');
var concat = require('gulp-concat');


gulp.task('less-dev', function () {
    gulp.src('src/main/webapp/content/templates/web1/styles/less/all.less')
        .pipe(less())
        .pipe(sourcemaps.init())

        // .pipe(autoprefixer({
        //     browsers: [
        //         'Android >= 2.3',
        //         'BlackBerry >= 7',
        //         'Chrome >= 9',
        //         'Firefox >= 4',
        //         'Explorer >= 9',
        //         'iOS >= 5',
        //         'Opera >= 11',
        //         'Safari >= 5',
        //         'OperaMobile >= 11',
        //         'OperaMini >= 6',
        //         'ChromeAndroid >= 9',
        //         'FirefoxAndroid >= 4',
        //         'ExplorerMobile >= 9'
        //     ]
        // }))
        .pipe(concat('style.css'))
        .pipe(minifycss())
        .pipe(sourcemaps.write())
        .pipe(gulp.dest('src/main/webapp/content/templates/web1/styles/css'))

});


gulp.task('less', function () {
    gulp.src('src/main/webapp/content/templates/web1/styles/less/all.less')
        .pipe(less())
        .pipe(autoprefixer({
            browsers: [
                'Android >= 2.3',
                'BlackBerry >= 7',
                'Chrome >= 9',
                'Firefox >= 4',
                'Explorer >= 9',
                'iOS >= 5',
                'Opera >= 11',
                'Safari >= 5',
                'OperaMobile >= 11',
                'OperaMini >= 6',
                'ChromeAndroid >= 9',
                'FirefoxAndroid >= 4',
                'ExplorerMobile >= 9'
            ]
        }))
        .pipe(concat('style.css'))
        .pipe(minifycss())
        // .pipe(bless({
        //     imports: true
        // }))
        .pipe(gulp.dest('src/main/webapp/content/templates/web1/styles/css'))
        /*.pipe(notify({
         message: 'Successfully compiled website LESS'
         }))*/;
});


// Clean
gulp.task('clean', function () {
    gulp.src([
            'src/main/webapp/content/templates/web1/styles/css'], {read: false})
        .pipe(clean());
});


//Default task
gulp.task('default', ['clean','less'], function () {
    // gulp.run('less', 'lint', 'js');
    // gulp.run('less-website', 'less-email-inline', 'less-email-non-inline', 'js');
});


gulp.task('dev', ['less-dev']);


function onError(err) {
    console.log(err);
    this.emit('end');
}
