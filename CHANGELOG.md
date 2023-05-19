# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.6.0 - 2023-05-19
### Added
- Show CI status on draft pull requests
- Optional field to replace ci status for changesets with pull request ci status

## 2.5.1 - 2022-09-15
### Fixed
- Pull request table column permission check ([#56](https://github.com/scm-manager/scm-ci-plugin/pull/56))
- Loading spinner for ci status for closed pull requests in pull request table ([#59](https://github.com/scm-manager/scm-ci-plugin/pull/59))

## 2.5.0 - 2022-08-05
### Added
- CI-Status information in pr table ([#55](https://github.com/scm-manager/scm-ci-plugin/pull/55))

### Changed
- Use glob patterns for name in rules ([#52](https://github.com/scm-manager/scm-ci-plugin/pull/52))

## 2.4.1 - 2022-05-13
### Fixed
- Do not fail with old configuration ([#53](https://github.com/scm-manager/scm-ci-plugin/pull/53))

## 2.4.0 - 2022-05-12
### Added
- Option to all rules to ignore status on changesets ([#49](https://github.com/scm-manager/scm-ci-plugin/pull/49))

## 2.3.3 - 2022-02-17
### Fixed
- NullPointerException thrown for unknown revision ([#43](https://github.com/scm-manager/scm-ci-plugin/pull/43))

## 2.3.2 - 2022-01-12
### Fixed
- CI status error for closed pull requests ([#40](https://github.com/scm-manager/scm-ci-plugin/pull/40))

## 2.3.1 - 2022-01-07
### Fixed
- High contrast mode findings ([#39](https://github.com/scm-manager/scm-ci-plugin/pull/39))

## 2.3.0 - 2021-12-23
### Added
- Show ci information on branch details and overview ([#37](https://github.com/scm-manager/scm-ci-plugin/pull/37))

### Changed
- Fetch ci status via react query

## 2.2.1 - 2020-11-03
### Fixed
- Hide analyses on closed pull request ([#33](https://github.com/scm-manager/scm-ci-plugin/pull/33))

## 2.2.0 - 2020-12-17
### Added
- Sort Keys for Workflow Rules ([#13](https://github.com/scm-manager/scm-ci-plugin/pull/13))
- Mark read only verbs to be able to see ci status in archived repositories ([#16](https://github.com/scm-manager/scm-ci-plugin/pull/16))

## 2.1.0 - 2020-07-03
### Added
- Documentation in English and German ([#11](https://github.com/scm-manager/scm-ci-plugin/pull/11))

### Fixed
- Workflow engine rules also consider pull request ci status for validation ([#12](https://github.com/scm-manager/scm-ci-plugin/pull/12))

## 2.0.0 - 2020-06-04
### Changed
- Rebuild for api changes from core

## 2.0.0-rc3 - 2020-05-08
### Added
- Add "CI-Status All Success" rule for workflow engine ([#7](https://github.com/scm-manager/scm-ci-plugin/pull/7))
- Implemented X CI-Status successful rule for workflow engine ([#8](https://github.com/scm-manager/scm-ci-plugin/pull/8))
- Add swagger rest annotations to generate openAPI specs for the scm-openapi-plugin. ([#2](https://github.com/scm-manager/scm-ci-plugin/pull/5))
- Integrate pull request specific ci statuses ([#10](https://github.com/scm-manager/scm-ci-plugin/pull/10))

### Changed
- Changeover to MIT license ([#6](https://github.com/scm-manager/scm-ci-plugin/pull/6))

