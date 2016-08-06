# Distributed System Programming, final project
The assignment description is [here](https://www.cs.bgu.ac.il/~dsp162/Assignments/Assignment_3)

Status: [![CircleCI](https://circleci.com/gh/hagai-lvi/dsp-final-project.svg?style=svg&circle-token=e14fa6380e816fd1665baa9cf38466fdb238838a)](https://circleci.com/gh/hagai-lvi/dsp-final-project)

# Map reduce steps:
First, we will define the term **"abstract path"** to be a path that does not include the nouns in the outermost nodes,
and a **"concrete path"** to be a path that includes the nouns in the outermost nodes, i.e, specific to 2 nouns.

1. The first step extracts abstract paths from the input, and filters only relevant concrete paths.
We write each concrete path with its nouns and count, and we write each abstract path with all the nouns with which
we have seen it.
Then, in the reduce step, we aggregate all the counts, in (unlikely) case in which a concrete path showed more
than once.
In addition, for abstract paths, we use a partitioner that points all the abstract paths to a single node, so that
we will be able to give them serial numbers. For each abstract path, we count with how many nouns it appeared,
and filter only the relevant ones (defined by the DPMin).
An example output line for an abstract path will be
`abstract-path appos august/NNP pobj of/IN prep  2`, which means that we have seen the
abstract path `appos august/NNP pobj of/IN prep`, and gave it the UID `2`.
An exmple for an output line fo a concrete path will be `tree zinc/NN nn metabol/NN      12`,
which means that we have seen the path `zinc/NN nn metabol/NN` 12 times.

2. In our second MR step, we extract all the word pairs to an output file called `wordpairs`, and for each
pair, with which paths we have seen it to a file called `paths`.
For each concrete path, the **mapper** writes the concrete pair of words, i.e. `<key=<nouns>, value="">`.
In addition, we write the abstract path with which we have seen the pair, together with the pair itself,
i.e. `<key=<abstract-path>, value=<nouns>>`.
For abstract paths, we write the abstract path with its id, i.e. `<key=<abstract-path>, value=<path-id>>`.
We use an asterisk (`*`) and a plus sign (`+`), to differentiate between abstract paths and concrete path,
and to make sure that an abstract path will get to the reducer, before all the concrete paths that used this
abstract path.
We use a **partitioner**, that sends all the concrete paths to the same partition as the corresponding
abstract path.
The **reducer** sends word pairs like they are to the `wordpairs` output file.
If it sees an abstract path, it saves it (only one abstract path at a time).
If it sees a concrete path, it makes sure that it matches to the current abstract path, and if it does,
the reducer emits the pair of words with the path id, to the `paths` output file.
An exmple to a line in the `paths` output file is `live faith	0`, which means that we have seen the
pair `live faith`, with the abstract path whose serial number is `0`.
An example for a line in the `wordpairs` file, is `zinc metabol` which means that we have seen this
pair in the input at least once.
