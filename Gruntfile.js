module.exports = function (grunt) {
    grunt.initConfig({
        clean: ['src/main/webapp/lib'],
        copy: {
            jquery: {
                files: [
                    {
                        expand: true,
                        cwd: 'bower_components/jquery/dist/',
                        src: 'jquery.min.js',
                        dest: 'src/main/webapp/lib/jquery/'
                    }
                ]
            },
            bootstrap: {
                files: [
                    {
                        expand: true,
                        cwd: 'bower_components/bootstrap/dist/',
                        src: '**',
                        dest: 'src/main/webapp/lib/bootstrap/'
                    }
                ]
            },
            pnotify: {
                files: [
                    {
                        expand: true,
                        cwd: 'bower_components/pnotify/dist/',
                        src: 'pnotify.css',
                        dest: 'src/main/webapp/lib/pnotify/'
                    },
                    {
                        expand: true,
                        cwd: 'bower_components/pnotify/dist/',
                        src: 'pnotify.js',
                        dest: 'src/main/webapp/lib/pnotify/'
                    }
                ]
            },
            jsSHA: {
                files: [
                    {
                        expand: true,
                        cwd: 'bower_components/jsSHA/src/',
                        src: 'sha1.js',
                        dest: 'src/main/webapp/lib/jssha/'
                    }
                ]
            },
            uuid:{
                files: [
                    {
                        expand: true,
                        cwd: 'bower_components/uuid-js/lib/',
                        src: 'uuid.js',
                        dest: 'src/main/webapp/lib/uuid/'
                    }
                ]
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-clean');

    grunt.registerTask('lib', ['clean', 'copy']);

};