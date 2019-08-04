/*
node { //
   def mvnHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      git 'https://github.com/jpilgrim/lift.git'
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.           
      mvnHome = tool 'M3'
   }
   stage('Build') {
      // Run the maven build
      withEnv(["MVN_HOME=$mvnHome"]) {
         if (isUnix()) {
            sh '"$MVN_HOME/bin/mvn" -Dmaven.test.failure.ignore clean package'
         } else {
            bat(/"%MVN_HOME%\bin\mvn" -Dmaven.test.failure.ignore clean package/)
         }
      }
   }
   stage('Results') {
      junit '** /target/surefire-reports/TEST-*.xml'
      archiveArtifacts 'target/*.jar'
   }
}
*/

pipeline {
    agent any
    /*
    options {
        buildDiscarder(
            logRotator(
                numToKeepStr:          '20',
                artifactDaysToKeepStr: '14',
                artifactNumToKeepStr:  '20',
                daysToKeepStr:         '14'))
        disableConcurrentBuilds()
        timeout(time: 6, unit: 'HOURS')
        timestamps()
    }
   */
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

    // triggers {
    //    cron('H 23 * * *') // Nightly build every day a 11pm (23:00)
    // }

    stages {

        stage('Build & Test') {
            steps {
                wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
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
            }
        }

        /*
        stage('Sign') {
            steps {
                dir('n4js/builds/org.eclipse.n4js.product.build/target/products') {
                    sh "pwd"

                    // sign the macOS product
                    sh """\
                        mv      n4jside-macosx.cocoa.x86_64.zip          unsigned_n4jside-macosx.cocoa.x86_64.zip
                        curl -o n4jside-macosx.cocoa.x86_64.zip -F file=@unsigned_n4jside-macosx.cocoa.x86_64.zip http://build.eclipse.org:31338/macsign.php
                        rm unsigned_n4jside-macosx.cocoa.x86_64.zip
                    """

                    // bundle macOS product into a signed .dmg image
                    sh """\
                        curl -o n4jside-macosx.cocoa.x86_64.dmg -F sign=true -F source=@n4jside-macosx.cocoa.x86_64.zip http://build.eclipse.org:31338/dmg-packager
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'START: moving old products / update sites to archive.eclipse.org (those that are older than a week):'
                sh """\
                    cd /home/data/httpd/download.eclipse.org/n4js
                    find products/nightly -maxdepth 1 -type d -name 20\\* -mtime +7 -print -exec mv {} /home/data/httpd/archive.eclipse.org/n4js/{} \\;
                    find updates/nightly  -maxdepth 1 -type d -name 20\\* -mtime +7 -print -exec mv {} /home/data/httpd/archive.eclipse.org/n4js/{} \\;
                """
                echo 'END: moving old products / update sites to archive.eclipse.org'

                echo 'START: copying new products / update site to download.eclipse.org:'
                sh """\
                    mkdir  /home/data/httpd/download.eclipse.org/n4js/products/nightly/$TIMESTAMP
                    cp     n4js/builds/org.eclipse.n4js.product.build/target/products/*.zip         /home/data/httpd/download.eclipse.org/n4js/products/nightly/$TIMESTAMP
                    cp     n4js/builds/org.eclipse.n4js.product.build/target/products/*.dmg         /home/data/httpd/download.eclipse.org/n4js/products/nightly/$TIMESTAMP

                    rm -rf /home/data/httpd/download.eclipse.org/n4js/products/nightly/LATEST
                    mkdir  /home/data/httpd/download.eclipse.org/n4js/products/nightly/LATEST
                    cp -R  /home/data/httpd/download.eclipse.org/n4js/products/nightly/$TIMESTAMP/* /home/data/httpd/download.eclipse.org/n4js/products/nightly/LATEST

                    mkdir  /home/data/httpd/download.eclipse.org/n4js/updates/nightly/$TIMESTAMP
                    cp -R  n4js/builds/org.eclipse.n4js.product.build/target/repository/*           /home/data/httpd/download.eclipse.org/n4js/updates/nightly/$TIMESTAMP

                    rm -rf /home/data/httpd/download.eclipse.org/n4js/updates/nightly/LATEST
                    mkdir  /home/data/httpd/download.eclipse.org/n4js/updates/nightly/LATEST
                    cp -R  /home/data/httpd/download.eclipse.org/n4js/updates/nightly/$TIMESTAMP/*  /home/data/httpd/download.eclipse.org/n4js/updates/nightly/LATEST
                """
                echo 'END: copying new products / update site to download.eclipse.org'

                // show a summary
                script {
                    def output = sh returnStdout: true, script: """\
                        echo ******************** Products on download server:
                        ls -l /home/data/httpd/download.eclipse.org/n4js/products/nightly
                        echo ******************** Update sites on download server:
                        ls -l /home/data/httpd/download.eclipse.org/n4js/updates/nightly
                        echo ******************** Products on archive server:
                        ls -l /home/data/httpd/archive.eclipse.org/n4js/products/nightly
                        echo ******************** Update sites on archive server:
                        ls -l /home/data/httpd/archive.eclipse.org/n4js/updates/nightly
                        echo ********************
                    """

                    echo "$output"
                }
            }
        } */
    }

    post {
        always {
         //   archiveArtifacts allowEmptyArchive: true, artifacts: '**/builds/**/target/products/*.zip'
         //   archiveArtifacts allowEmptyArchive: true, artifacts: '**/tools/**/target/n4jsc.jar'
         //   archiveArtifacts allowEmptyArchive: true, artifacts: '**/logs/*.log'
         //   archiveArtifacts allowEmptyArchive: true, artifacts: '**/tests/**/target/**/*-output.txt'

            junit '**/surefire-reports/**/*.xml'
            //junit '**/failsafe-reports/**/*.xml'
        }
        cleanup {
            // excute here in case archiving fails in 'always'
            //mail to: 'some.one@some.where.eu',
            //     subject: "${currentBuild.result}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            //     body:  """\
            //            ${currentBuild.result}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
            //            Check console output at '${env.BUILD_URL}'
            //            """

            // Execute after every other post condition has been evaluated, regardless of status
            // See https://jenkins.io/doc/book/pipeline/syntax/#post
            // echo 'Cleaning up workspace'
            // deleteDir()
         }
    }
}