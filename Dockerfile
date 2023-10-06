FROM jenkins/jenkins:latest

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
ENV CASC_JENKINS_CONFIG /var/jenkins_home/casc_configs

RUN jenkins-plugin-cli --plugins "email-ext emailext-template configuration-as-code"

RUN mkdir /var/jenkins_home/casc_configs && mkdir /var/jenkins_home/job_definitions && mkdir /var/jenkins_home/.config && mkdir /var/jenkins_home/.config/jenkins_jobs
COPY jenkins-casc.yaml /var/jenkins_home/casc_configs/jenkins.yaml
COPY job_definition.yaml /var/jenkins_home/job_definitions/job_definitions.yaml
COPY jenkins_jobs.ini /var/jenkins_home/.config/jenkins_jobs/jenkins_jobs.ini

USER root
RUN apt update && apt install -y jq python3-pip python3.11-venv python3-full vim
RUN python3 -m venv .venv && . .venv/bin/activate && pip install jenkins-job-builder

USER jenkins

# build: docker build -t jenkins:jcasc .
# run: docker run -it --name jenkins --rm -p 8080:8080 jenkins:jcasc

# exec: 
#  ID=$(docker container ls -q -f name=^jenkins$)
#  docker exec -it $ID /bin/bash

# exec jenkins-job-builder: 
#   . .venv/bin/activate
#   jenkins-jobs update job_definitions/job_definitions.yaml 
