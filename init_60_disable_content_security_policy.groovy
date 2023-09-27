#!groovy

import hudson.model.*
import jenkins.model.*

System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "default-src 'self' 'unsafe-inline' 'unsafe-eval'; img-src 'self' 'unsafe-inline' data:;")
println("hudson.model.DirectoryBrowserSupport.CSP=" + System.getProperty("hudson.model.DirectoryBrowserSupport.CSP"))
