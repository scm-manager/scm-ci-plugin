---
title: Status Bar
---
With the review plugin, SCM-Manager supports pull requests. The scm-ci-plugin can link into the pull requests and enrich them with useful information.

In the pull request details a status bar shows information from all CI systems that are integrated for this pull request.

With only one Jenkins instance and one build job for a branch it could look like this:

![Pull Request Statusbar](assets/statusbar-pending.png)

This status bar could appear in these variations for each build job status: 

### Running
The Jenkins job run has started but not finished yet.

![Statusbar Pending](assets/status-pending.png)

### Successful
The Jenkins build job run was successful and is green in Jenkins.

![Statusbar Successful](assets/status-success.png)

### Unstable
The Jenkins build job is yellow. Maybe unit tests failed.

![Statusbar Unstable](assets/status-unstable.png)

### Faulty
The Jenkins build job is red. The run could not be completed because an error occurred.

![Statusbar Unstable](assets/status-failure.png)

Even if only one of the analyses is faulty, the status bar would be shown as red. Only if all analyses are completed without any errors it is shown as green.
