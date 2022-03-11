# axe-core-maven-html

[![CircleCI](https://circleci.com/gh/dequelabs/axe-core-maven-html.svg?style=svg&circle-token=5bd96056d8ab9f52737de9b5d7cc614decbb9819)](https://circleci.com/gh/dequelabs/axe-core-maven-html)
[![Join our Slack chat](https://img.shields.io/badge/slack-chat-purple.svg?logo=slack)](https://accessibility.deque.com/axe-community)

This repository contains 2 packages, which can be used for automated accessibility testing powered by [axe core][axe-core].

The packages are listed below:

- [`Selenium`](selenium/README.md)
- [`Playwright`](playwright/README.md)

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
