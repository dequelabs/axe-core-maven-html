# axe-core-maven-html

[![Join our Slack chat](https://img.shields.io/badge/slack-chat-purple.svg?logo=slack)](https://accessibility.deque.com/axe-community)

This repository contains 2 packages, which can be used for automated accessibility testing powered by [axe core][axe-core].

The packages are listed below:

- [`Selenium`](selenium/README.md)
- [`Playwright`](playwright/README.md)

## Verifying releases

Artifacts published to Maven Central are signed with Deque's code-signing key:

```
Deque Systems, Inc. <helpdesk@deque.com>
F8AF 74A4 CDA6 E206 3C89  85E2 1A4A C53E FF73 AC0F
```

Fetch the key and check the `.asc` that accompanies the artifact:

```console
gpg --keyserver keyserver.ubuntu.com --recv-keys F8AF74A4CDA6E2063C8985E21A4AC53EFF73AC0F
gpg --verify selenium-4.13.0.jar.asc selenium-4.13.0.jar
```

Check that the signature is from the fingerprint above: that fingerprint,
published here, is what identifies the key as ours.

Earlier releases were signed with key
`7701193A898A849383D3E8B49F8AFEACBF07F7C4`, which has not been revoked, so they
still verify.

## Development

Install root dependencies:

```console
npm install
```

Please refer to respective README for installation, usage, and configuration notes.

## Philosophy

We believe that automated testing has an important role to play in achieving digital equality and that in order to do that, it must achieve mainstream adoption by professional web developers. That means that the tests must inspire trust, must be fast, must work everywhere and must be available everywhere.

## Manifesto

1. Automated accessibility testing rules must have a zero false positive rate
2. Automated accessibility testing rules must be lightweight and fast
3. Automated accessibility testing rules must work in all modern browsers
4. Automated accessibility testing rules must, themselves, be tested automatically

[axe-core]: https://github.com/dequelabs/axe-core

## Contributing

In order to contribute, you must accept the [contributor licence agreement](https://cla-assistant.io/dequelabs/axe-selenium-java) (CLA). Acceptance of this agreement will be checked automatically and pull requests without a CLA cannot be merged.

## Deployment (Maintainers Only)

This package is deployed to Maven Central via OSSRH. To deploy this package, follow [these instructions on StackOverflow](https://stackoverflow.com/a/42917618).

Additionally add your OSSRH credentials to your `~/.m2/settings.xml` file as such:

```xml
<servers>
  <server>
    <id>ossrh</id>
    <username>YOUR_OSSRH_JIRA_USERNAME</username>
    <password>YOUR_OSSRH_JIRA_PASSWORD</password>
  </server>
</servers>
```
