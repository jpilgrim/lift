pipeline {
    agent any

    environment {
        NODEJS_PATH= '/usr/bin' // '/shared/common/node-v10.15.3-linux-x64/bin'
        YARN_PATH  = '/usr/bin' // '/shared/common/yarn/1.15.2/bin'
        PATH       = "${PATH}"                                          +
                     ":/opt/apache-ant-1.0.6/bin"             +
                     ":/opt/apache-maven-3.6.1/bin/"
                     // ":/shared/common/node-v10.15.3-linux-x64/bin"      +
                     // ":/shared/common/java/openjdk/jdk11-x64/bin"
        MAVEN_OPTS = '-Xmx4G'
        JAVA_HOME  = '/usr/lib/jvm/java-11-openjdk-amd64'
        TIMESTAMP  = new Date().format("yyyyMMddHHmm")
   }

    stages {
        stage('Build and Test') {
            steps {
                echo 'Building and testing..'
                dir('lift') {
                    script {
                        def options = [
                            '--batch-mode',
                            //'--quiet',
                            '--update-snapshots',
                            '--show-version',
                            '-Dtycho.localArtifacts=ignore',
                            '-Dmaven.test.failure.ignore',
                            '-DWORKSPACE=' + env.WORKSPACE,
                            '-DexcludeJRE'
                        ].join(' ')
                        def profiles = [
                            'buildProduct',
                            'execute-plugin-tests',
                            'execute-plugin-ui-tests',
                            'execute-ecma-tests',
                            'execute-accesscontrol-tests',
                            'execute-smoke-tests'
                         //'execute-hlc-integration-tests'
                        ].join(',')

                        sh """\
                            pwd
                            git log -n 1
                            npm version
                        """
                        sh "mvn clean verify -P${profiles} ${options}"

                      // sh "ls -Ral builds/org.eclipse.n4js.product.build/target/repository/"
                    }
                }
            }
        } // end stage
        /*
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }*/
    }
}