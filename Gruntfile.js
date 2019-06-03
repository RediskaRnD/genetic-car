module.exports = function(grunt) {
    grunt.initConfig({
        clean: {
            js: ['web-server/src/main/resources/**/*.js',
                'web-server/src/main/resources/tools/*.ts',
                'web-server/src/main/resources/core/*.ts',
                'web-server/src/main/resources/**/*.map',
                '!web-server/src/main/resources/**/require.js'],
            debug: {}
        },
        ts: {
            default : {
                options: {
                    rootDir: 'web-server/src/main/resources/static'
                },
                tsconfig: 'web-server/src/main/resources/tsconfig.json'
            }
        }
    });
    grunt.loadNpmTasks("grunt-contrib-clean");
    grunt.loadNpmTasks("grunt-ts");
    grunt.registerTask("default", ["clean", "ts"]);
};