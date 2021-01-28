# gojulmq4j-api

gojulmq4j-api provides an abstraction over message queues so that anyone can be abstracted from the MQ actual
implementation. The goal is to make it possible to abstract yourself from a specific MQ implementation and have your MQ
as an implementation detail.

## Usage examples

For each module there's a corresponding test program, in the <module>-test subproject. We sadly must do this instead of
using proper unit tests because these test programs need a MQ broker up and running prior to being run.  
