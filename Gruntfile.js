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
            md5: {
                files: [
                    {
                        expand: true,
                        cwd: 'bower_components/js-md5/build/',
                        src: 'md5.min.js',
                        dest: 'src/main/webapp/lib/md5/'
                    }
                ]
            },
            uuid: {
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