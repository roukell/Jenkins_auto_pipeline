import hudson.model.*
import jenkins.model.*
import hudson.model.FreeStyleProject
import hudson.plugins.ansicolor.AnsiColorBuildWrapper
import hudson.plugins.git.GitSCM
import hudson.plugins.git.UserRemoteConfig
import hudson.plugins.git.BranchSpec
import hudson.plugins.timestamper.TimestamperBuildWrapper
import hudson.tasks.Shell
import hudson.triggers.SCMTrigger
import javaposse.jobdsl.plugin.*
import jenkins.model.Jenkins
import org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper
import org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordBinding

println "--> create seed jobs"

def jenkins = Jenkins.getInstance()
def jobName = "master_seed"

if (jenkins.getItem(jobName) == null) {
  def displayName = "MasterSeed"
  def triggerSpec = "* * * * *"
  def targets = ["jobs/**/*_job.groovy", "jobs/**/*_pipelines.groovy"]

  scmTrigger = new SCMTrigger(triggerSpec)
  dslProject = new hudson.model.FreeStyleProject(jenkins, jobName)

  println "--> setting job details"
  dslProject.displayName = displayName
  dslProject.setDescription("""
        Warning: This build is managed by job-dsl. Any modifications will be overwritten.
        This job is responsible for generating all other Jenkins jobs via definitions in the jenkins-job-dsl folder
        """.stripIndent());

    println "--> adding trigger to job"
    dslProject.addTrigger(scmTrigger)

    // dslProject.buildersList.add(new Shell('gradle clean test'))

    println "--> adding builders to job"
    for (target in targets) {
        dslBuilder = new ExecuteDslScripts()
        dslBuilder.setTargets(target)
        dslBuilder.setUseScriptText(false)
        dslBuilder.setSandbox(false)
        dslBuilder.setIgnoreExisting(false)
        dslBuilder.setIgnoreMissingFiles(false)
        dslBuilder.setFailOnMissingPlugin(false)
        dslBuilder.setUnstableOnDeprecation(false)
        dslBuilder.setRemovedJobAction(RemovedJobAction.IGNORE)
        dslBuilder.setRemovedViewAction(RemovedViewAction.IGNORE)
        dslBuilder.setLookupStrategy(LookupStrategy.JENKINS_ROOT)
        // dslBuilder.setAdditionalClasspath("src/main/groovy")
        dslProject.getBuildersList().add(dslBuilder)
    }

    println "--> adding wrappers to job"
    ansiColourWrapper = new AnsiColorBuildWrapper("xterm")
    timestampWrapper = new TimestamperBuildWrapper()
 
    dslProject.getBuildWrappersList().add(ansiColourWrapper)
    dslProject.getBuildWrappersList().add(timestampWrapper)
 
    println "--> adding job definition to Jenkins"
    jenkins.add(dslProject, jobName);
 
    println "--> enabling job trigger"
    scmTrigger.start(dslProject, true);
} else {
  println "--> job ${jobName} already exists"
}
