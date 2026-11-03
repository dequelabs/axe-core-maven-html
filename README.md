# axe-core-maven-html

[![Join our Slack chat](https://img.shields.io/badge/slack-chat-purple.svg?logo=slack)](https://accessibility.deque.com/axe-community)

This repository contains 2 packages, which can be used for automated accessibility testing powered by [axe core][axe-core].

The packages are listed below:

- [`Selenium`](selenium/README.md)
- [`Playwright`](playwright/README.md)

## Verifying releases

Artifacts published to Maven Central are signed with Deque's code-signing key, published on the keyservers under `Deque Systems, Inc. <helpdesk@deque.com>`. Fetch it, then check the `.asc` that accompanies the artifact:

```console
gpg --keyserver keyserver.ubuntu.com --search-keys helpdesk@deque.com
gpg --verify selenium-<version>.jar.asc selenium-<version>.jar
```

The key is also listed at <https://keyserver.ubuntu.com/pks/lookup?search=helpdesk%40deque.com&op=index>. Older releases may be signed with an earlier Deque key under the same identity; signing keys are not revoked when they are replaced, so those releases still verify.

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

GitHub Actions publishes from the [`CI` workflow](.github/workflows/ci.yml) once every test passes:

- A push to `develop` or `release` publishes a snapshot to the Maven Central snapshot repository.
- A push to `master` publishes a release to Maven Central, then creates the GitHub release with the version's changelog and the signed artifacts attached.

The artifacts are built and tested once on Java 8, and stored on the workflow run. The deploy jobs sign those stored artifacts and upload them. They don't rebuild anything.

The deploy jobs run in the `central.sonatype.com` environment, which only `develop`, `release` and `master` can use. It holds these secrets and variable:

| Secret | Contents |
| --- | --- |
| `CENTRAL_TOKEN_USERNAME` | Central Publisher Portal user token name |
| `CENTRAL_TOKEN_PASSWORD` | Central Publisher Portal user token password |
| `GPG_PRIVATE_KEY` | Base64-encoded export of the signing key |
| `GPG_PASSPHRASE` | Passphrase for the signing key |

| Variable | Contents |
| --- | --- |
| `GPG_KEY_FINGERPRINT` | Full fingerprint of the signing key; the deploy jobs refuse to sign with any other key |

Jobs that need the team's personal access token (the GitHub release, the release candidate, the master-to-develop sync and the axe-core update) run in the `PAT` environment, which only `develop` and `master` can use. They read the token from that environment's `TOKEN` secret.
