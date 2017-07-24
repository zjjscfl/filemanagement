module.exports = function (grunt) {
    grunt.initConfig({
        copy: {
            jquery: {
                files: [
                    {
                        expand: true,
                        cwd: 'bower_components/jquery/dist/',
                        src: 'jquery.js',
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
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');


    grunt.registerTask('lib', ['copy']);

};