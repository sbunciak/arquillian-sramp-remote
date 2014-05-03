arquillian-sramp-remote
=======================

This project consist of S-RAMP remote container adapter implementation for Arquillian. This Arquillian extension will enable users to deploy artifacts to S-RAMP Repository before running their Arquillian tests.

## Summary

Basically, _arquillian-sramp-remote_ converts ShrinkWrap archive into S-RAMP compatible archive, and deploy it to proper S-RAMP context. 

This might become handy when developing e.g. custom artifact deriver for S-RAMP (or other form of inter-product integration) and would like to test it, or when creating custom governance workflows for DTGov. In that case you can leverage feature of this extension to inject SrampAtomApiClient and/or DTGov TaskApiClient as ArquillianResource to your test classes.

For more information checkout wiki [page](https://github.com/sbunciak/arquillian-sramp-remote/wiki/About).

## Usage

How to set up a project to use _arquillian-sramp-remote_ extension, and how to write your tests using it, is described in this [guide](https://github.com/sbunciak/arquillian-sramp-remote/wiki/Usage)
