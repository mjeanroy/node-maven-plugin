/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 Mickael Jeanroy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

var path = require('path');
var gulp = require('gulp');
var jshint = require('gulp-jshint');
var bower = require('gulp-bower');
var uglify = require('gulp-uglify');
var htmlmin = require('gulp-htmlmin');
var rimraf = require('gulp-rimraf');
var rename = require("gulp-rename");
var karma = require('karma');
var KarmaServer = karma.Server;

gulp.task('clean', function() {
  return gulp.src('target/webapp', { read: false })
    .pipe(rimraf());
});

gulp.task('bower', function() {
  return bower({
    cmd: 'update'
  });
});

gulp.task('lint', function() {
  return gulp.src('src/main/webapp/js/**/*.js')
    .pipe(jshint())
    .pipe(jshint.reporter('default'))
});

gulp.task('test', function(done) {
  var conf = {
    configFile: path.join(__dirname, 'karma.conf.js')
  };

  var server = new KarmaServer(conf, done);
  server.start();
});

gulp.task('vendors', function() {
  return gulp.src('src/main/webapp/vendors/**/*')
    .pipe(gulp.dest('target/webapp/vendors'));
});

gulp.task('scripts', function() {
  return gulp.src('src/main/webapp/app/app.js')
    .pipe(uglify())
    .pipe(rename('app.min.js'))
    .pipe(gulp.dest('target/webapp/app'));
});

gulp.task('html', function() {
  return gulp.src('src/main/webapp/**/*.html')
    .pipe(htmlmin())
    .pipe(gulp.dest('target/webapp'));
});

gulp.task('build', ['vendors', 'scripts', 'html']);
