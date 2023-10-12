#!groovy

import hudson.model.*
import jenkins.model.*
import jenkins.security.*
import jenkins.security.apitoken.*
import java.lang.Thread

// script parameters
def env = System.getenv()
def userName = 'admin'
def tokenName = 'kb-token'

def user = User.get(userName, false)
def apiTokenProperty = user.getProperty(ApiTokenProperty.class)
def result = apiTokenProperty.tokenStore.generateNewToken(tokenName)
user.save()

return result.plainValue
