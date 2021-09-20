# [4.3.0](https://github.com/dequelabs/axe-core-maven-html/compare/v4.2.2...v4.3.0) (2021-09-20)


### Features

* add legacy mode option ([#175](https://github.com/dequelabs/axe-core-maven-html/issues/175)) ([fa10f39](https://github.com/dequelabs/axe-core-maven-html/commit/fa10f39d206696b9a55957fbb8e8c059949cb18b))


### Performance Improvements

* Optimize iframe recursion ([#168](https://github.com/dequelabs/axe-core-maven-html/issues/168)) ([292ebc9](https://github.com/dequelabs/axe-core-maven-html/commit/292ebc9648a66f5c1b9c24a7fcc97116554621d0))



## [4.2.2](https://github.com/dequelabs/axe-core-maven-html/compare/v4.2.1...v4.2.2) (2021-06-22)


### Features

* update to use `axe-core@4.2.2` ([#166](https://github.com/dequelabs/axe-core-maven-html/issues/166)) ([7685a72](https://github.com/dequelabs/axe-core-maven-html/commit/7685a72e2dd37c895071309b5f39aabf7744c65b))
* update to use `axe-core@4.2.3` ([#169](https://github.com/dequelabs/axe-core-maven-html/issues/169)) ([0ef3f52](https://github.com/dequelabs/axe-core-maven-html/commit/0ef3f52eae41375c77b980f05fffbdefcc85927c))



## [4.2.1](https://github.com/dequelabs/axe-core-maven-html/compare/v4.2.0...v4.2.1) (2021-05-18)


### Features

* update axe-core to 4.2.1 ([#163](https://github.com/dequelabs/axe-core-maven-html/issues/163)) ([1486bab](https://github.com/dequelabs/axe-core-maven-html/commit/1486babb4c8937251f453e4ed88b33f9e2a03bbc))



# [4.2.0](https://github.com/dequelabs/axe-core-maven-html/compare/v4.1.2...v4.2.0) (2021-05-05)


### Bug Fixes

* better iframe handling ([#154](https://github.com/dequelabs/axe-core-maven-html/issues/154)) ([d500d4f](https://github.com/dequelabs/axe-core-maven-html/commit/d500d4f0c0fd216d4bb54b6d2edd4ac2914131ec))
* do not error on iframe injection error ([#148](https://github.com/dequelabs/axe-core-maven-html/issues/148)) ([1bccd5b](https://github.com/dequelabs/axe-core-maven-html/commit/1bccd5bc78713d0c2ba479128a748759e2073c35))
* ignore iframe errors for non-loaded frames ([#145](https://github.com/dequelabs/axe-core-maven-html/issues/145)) ([ee9520a](https://github.com/dequelabs/axe-core-maven-html/commit/ee9520a43ccdf795fdb9c72d0516d4539a45fa6e))
* Recursively find <frame> ([#146](https://github.com/dequelabs/axe-core-maven-html/issues/146)) ([aae4836](https://github.com/dequelabs/axe-core-maven-html/commit/aae4836e90a6f2db7f4be5d40eb194edcf3fdf71))


### Features

* update axe-core to 4.2.0 ([#157](https://github.com/dequelabs/axe-core-maven-html/issues/157)) ([9d7eb41](https://github.com/dequelabs/axe-core-maven-html/commit/9d7eb4191aeb02e1083eb3a250f944c81824aa9b))



## [4.1.2](https://github.com/dequelabs/axe-core-maven-html/compare/v4.1.1...v4.1.2) (2021-02-23)


### Bug Fixes

* reporter prints element nodes ([#129](https://github.com/dequelabs/axe-core-maven-html/issues/129)) ([eae8302](https://github.com/dequelabs/axe-core-maven-html/commit/eae830291ee80b8e8fd1d2fa3e9604c68b80476c))


### Features

* add ability to disable iframe injection and checking ([#136](https://github.com/dequelabs/axe-core-maven-html/issues/136)) ([a628b61](https://github.com/dequelabs/axe-core-maven-html/commit/a628b61f7895549076476f9960a0cae54520dad8))
* add full context to axe errors ([#135](https://github.com/dequelabs/axe-core-maven-html/issues/135)) ([be44c8d](https://github.com/dequelabs/axe-core-maven-html/commit/be44c8dffd99493b8f49f5470e68325254b61d9b))
* bump axe-core to v4.1.2 ([#134](https://github.com/dequelabs/axe-core-maven-html/issues/134)) ([85e380b](https://github.com/dequelabs/axe-core-maven-html/commit/85e380b4e84c16a6aa382eb7dbea2e64e9e879f1))



## [4.1.1](https://github.com/dequelabs/axe-core-maven-html/compare/v4.1.0...v4.1.1) (2020-12-16)


### Bug Fixes

* sandbox buster is off by default ([#124](https://github.com/dequelabs/axe-core-maven-html/issues/124)) ([a7959c5](https://github.com/dequelabs/axe-core-maven-html/commit/a7959c5712aefae7fa003a75e956fa5275bc82a3))



# [4.1.0](https://github.com/dequelabs/axe-core-maven-html/compare/v4.0.0...v4.1.0) (2020-11-24)


### Features

* ability to remove sandbox from iframes ([#86](https://github.com/dequelabs/axe-core-maven-html/issues/86)) ([9357957](https://github.com/dequelabs/axe-core-maven-html/commit/9357957a3f1e9bbf338b921e8db35c8041cf43e7))
* add ability to use list of WebElements  ([#58](https://github.com/dequelabs/axe-core-maven-html/issues/58)) ([7e518f4](https://github.com/dequelabs/axe-core-maven-html/commit/7e518f47f28a7af53fb48543eba18a5b8bbaa2c8))
* add axe-core v4 ([#85](https://github.com/dequelabs/axe-core-maven-html/issues/85)) ([52b4534](https://github.com/dequelabs/axe-core-maven-html/commit/52b453465c1e2e6ac6974c84c8d83e64be2d575f))
* bump axe-core to v4.1.0 ([0aeaa80](https://github.com/dequelabs/axe-core-maven-html/commit/0aeaa80820073c0fdcfe18e06611a9a946689153))
* bump axe-core to v4.1.1 ([#114](https://github.com/dequelabs/axe-core-maven-html/issues/114)) ([71421da](https://github.com/dequelabs/axe-core-maven-html/commit/71421daf9ff2edd6819c6e9350241c770ec6dbe8))
* release v4.0.0 ([a8ae689](https://github.com/dequelabs/axe-core-maven-html/commit/a8ae689cb06971225546ab04501d0360935b63ee))



# [3.1.0](https://github.com/dequelabs/axe-core-maven-html/compare/v3.0.0...v3.1.0) (2020-03-17)


### Bug Fixes

* pass axe correct context when using include+exclude ([#43](https://github.com/dequelabs/axe-core-maven-html/issues/43)) ([73a5009](https://github.com/dequelabs/axe-core-maven-html/commit/73a5009b22afad5243d60db5f0d751de7165519a))
* set minimum compiler source/target to java 7 ([8d19fed](https://github.com/dequelabs/axe-core-maven-html/commit/8d19fedb271975b2457a8e27856a44f601b5a110))



# [3.0.0](https://github.com/dequelabs/axe-core-maven-html/compare/v2.1.0...v3.0.0) (2019-02-08)


### Features

* add configurable script timeout ([#28](https://github.com/dequelabs/axe-core-maven-html/issues/28)) ([0a7d0b9](https://github.com/dequelabs/axe-core-maven-html/commit/0a7d0b9ef7520f587536caa543323b5a8e65042c)), closes [#17](https://github.com/dequelabs/axe-core-maven-html/issues/17)
* **aXe 3.0+:** remove references to axe.a11yCheck, bump versions of Selenium webdriver, update axe to 2.6.1 in test/resources ([43145e7](https://github.com/dequelabs/axe-core-maven-html/commit/43145e7e431272807017ea5bd0e29e032a55b456))
* update axe-core to v3.1.2 ([#23](https://github.com/dequelabs/axe-core-maven-html/issues/23)) ([914a506](https://github.com/dequelabs/axe-core-maven-html/commit/914a50693058c152891202d4fb9a764c8cbcf09b))



# 2.1.0 (2017-10-12)



