# Distributed System Programming, final project
The assignment description is [here](https://www.cs.bgu.ac.il/~dsp162/Assignments/Assignment_3)

Status: [![CircleCI](https://circleci.com/gh/hagai-lvi/dsp-final-project.svg?style=svg&circle-token=e14fa6380e816fd1665baa9cf38466fdb238838a)](https://circleci.com/gh/hagai-lvi/dsp-final-project)

# Map reduce steps:
1. First, we extract dependency paths from the input.
We write each dependency path with its nouns and count.
Then, in the reduce step, we aggregate all the counts, in (unlikely) case in which a path showed more
than once.
An example output line will be
`youth/NN pobj among/IN prep problem/NN 16` (the pattern is `<path>\t<count>`).
In this case, the path contains 3 nodes - `"youth"`,`"among"` and `"problem"`,
and it appeared `16` times. The direction of the path is `youth=> problem`.
The labels of the edges in the path are `pobj` and `prep`, respectively.
2. In our second MR step, we count how many times each dependency path appeared in
the whole corpus with unique nouns, so that we can filter out rare dependency paths.
We take each path and extract from it only the "inner parts" (i.e. we remove the outermost nodes).
In the reduce step, we count how many times each path appeared and write it only if it passes
the given threshold.
3. The third step, aggregates both of the previous steps.  
It outputs all the pairs that we have seen in the input, even pairs to which we didn't see any matching path.  
In addition, for each pair, it emits all the paths with which we have seen it, that have passed the filter in the previous step.  
For example, for the input:
```
tree youth/NN pobj of/IN prep %/NN	133
path pobj of/IN prep
tree hello/NN pobj is/IN prep %/NN	133
tree hello/NN pobj of/IN prep world/NN	133
```
this step will output to `wordpairs-r-00000`
```
hello %	
hello world	
youth %	
```
and to `paths-r-00000`, it will output
```
hello world	pobj of/IN prep
youth %	pobj of/IN prep
```
