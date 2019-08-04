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
            }
        }
        /*
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }*/
    }
}