# DataCollector

## Sentence Tasks

### Morphological Disambiguation
In this work, human annotators selected the correct morphological parsing among multiple possible analyses returned from the automatic parser. The tag set and morphological representation were adopted from the study. Each output of the parser comprises the root of the word, its part-of-speech tag and a set of morphemes, each separated with a '+' sign.

### Word Sense Disambiguation
In the annotation screen, there are some possible meanings under each word of each sentence. For tagging the words, the person (tagger) needs to choose the appropriate meaning for each word considering the items from combobox. When the tagger presses the 'next' button, his/her annotation is saved and the tagger can pass to the next one to annotate it. 

### Named Entity Annotation
In the entity annotation, the annotators annotate named entities in a sentence. Possible named entities are listed. If a word is not a named entity (a regular word, a punctuation, etc.), the user selects the tag “NONE”.

### Shallow Parsing Annotation
In the shallow parsing step, the annotators choose the correct shallow parse tag for each word in a sentence. Possible shallow parse tags are listed. If a word does not fall into one of the established categories of parse tags, the user selects the tag “HİÇBİRİ”. If there are multiple subsentences connected via conjunctions, such as ’ve’ (and) or ’veya’ (or), the user analyses these subsentences independently.

### Predicate Selection
In the verbal predicate selection, the annotators choose the verbal predicate in each sentence. If the predicate is a multi- word expression, the user needs to select all constituents of this expression with the tag “PREDICATE”.

Similar to the shallow parsing step, if there are multiple subsentences in a sentence, verbal predicates of all those sub- sentences are annotated “PREDICATE”.

If there is no verbal predicate in the sentence, the annotator leaves the sentence unmarked.

### Semantic Role Labeling
Given the verbal predicate(s) in a sentence, the annotators annotate semantic roles of words. The list of semantic roles are determined with respect to the frameset of the selected verbal predicate for that sentence.

## Parse Tree Tasks
The current implementation of the application is designed for the import of text files that adhere to our Penn Treebank style data format. Once a pre-processed sentence has been imported into the editor, the human annotator is presented with the visualized syntactic parse tree of that sentence.

### Morphological Disambiguation
In this work, human annotators selected the correct morphological parse from multiple possible analyses returned from the automatic parser. The tag set and morphological representation were quoted from the study. Each output of the parser comprises the root of the word, its part-of-speech tag and a set of morphemes, each separated with a + sign.

### Word-Sense Disambiguation
Annotators can click on leaf nodes (words), but they are not allowed to make any changes such as rotating or deleting nodes. When a word is selected, a drop-down list is displayed, in which all available TDK entries of the selected lemma are listed. In all-words annotation tasks, list of words to be tagged, and therefore also the candidate senses, are unpredictable. Our application handles sense extraction on behalf of the annotators.

Each sense result shown to the annotators is populated with its POS and a sample sentence (which are already available in the TDK dictionary). This becomes a considerable aid for the annotators in deciding which sense to assign to a target word. Moreover, sense options whose POS do not agree with the word's POS, are disabled (are shown but not selectable) to facilitate the task. Just after the selection of the most appropriate sense, the drop-down list is hidden and the ID of the submitted synset is displayed under the word.

### English-Turkish Tree Translation

In translating an English syntactic tree, we confine ourselves to two operations. We can permute the children of a node and we can replace the English word token at a leaf node. No other modification of the tree is allowed. In particular, we use the same set of tags and predicate labels in the non-leaf nodes and do not use new tags for the Turkish trees. Adding or deleting nodes are not allowed either. We use the \*NONE\* tag when we can not use any direct gloss for an English token. In itself, this operation corresponds to effectively mapping an English token to a null token.

#### Constituent and morpheme order
Majority of unmarked Turkish sentences have the SOV order. When translating English trees, we permute its shallow subtrees to reflect the change of constituent order in Turkish. Also, the agglutinative suffixes of Turkish words dictate the order when permuting the constituents which correspond to prepositions and particles.

#### The determiner “the”
There is no definite article in Turkish corresponding to “the”. Depending on the context, “the” is translated either as \*NONE\* or one of the demonstrative adjectives in Turkish.

#### Case markers
Turkish, being a fairly scrambling language, uses case markers to denote the syntactic functions of nouns and noun groups. For example, accusative case may be used to mark the direct object of a transitive verb and locative case may be used to mark the head of a prepositional phrase. In translation from English to Turkish, the prepositions are usually replaced with \*NONE\* and their corresponding case is attached to the nominal head of the phrase.

#### Plural in nouns and verb inflection
Number agreement between the verb in the predicate and the subject is somewhat loose in Turkish. We preserved this freedom in translation and chose the number inflection that sounds more natural. Also, plural nouns under NNS tag in the English tree are sometimes translated as singular. In those cases, we kept the original POS tag NNS intact but used the singular gloss.

#### Tense ambiguity
It is in general not possible to find an exact mapping among the tense classes in a pair of languages. When translating the trees, we mapped the English verb tenses to their closest semantic classes in Turkish while trying to keep the overall flow of the Turkish sentence natural. In many cases, we mapped the perfective tense in English to the past tense in Turkish. Similarly, we sometimes mapped the present tense to present continuous.

#### WH- Questions
Question sentences require special attention dur- ing transformation. As opposed to movement in English question sentences, any constituent in Turkish can be questioned by replacing it with an inflected question word. In the Penn Treebank II annotation, the movement leaves a trace and is associated with wh- constituent with a numeric marker. For example, “WHNP-17” and “\*T\*-17” are associated. When we translate the tree for a question sentence, we replace the wh- constituent with \*NONE\* and replace its trace with the appropriate question pronoun in Turkish.

#### Miscellany
In the translation of nominal clauses, the copula marker “-dIr” corresponding to verb “be” is often dropped.

The proper nouns are translated with their common Turkish gloss if there is one. So, “London” becomes “Londra”.

Subordinating conjunctions, marked as “IN” in English sentences, are transformed to \*NONE\* and the appropriate participle morpheme is appended to the stem in the Turkish translation.

A multiword expression may correspond to a single English word. Conversely, more than one words in English may correspond to a single word in Turkish. In the first case, we use the multiword expression as the gloss. In the latter case, we replace some English words with \*NONE\*.

### English-Persian Tree Translation

Persian language is a member of Indo-European family that uses a modified Arabic script and is written from right to left. As a result of this, processing Persian language becomes difficult. Beside this, tokenization is also difficult since delineating spaces are not consistently used. Persian has a flexible word order. Most commonly, sentences follow a subject-object-verb ordering. However, the ordering may change according to the emphasis. For example, if the emphasis is on the subject, one can use object-subject- verb ordering.

Forming question sentences is also different than English, in the sense that the structure of the sentence does not necessarily change. In Persian, affixes can come before or after word roots. Like English, verb form changes according to tense.

#### Constituent order and \*NONE\* cases
Majority of Persian sentences have the SOV order. Based on this fact, when translating English trees, we permute its subtrees to reflect change of constituent order in Persian.

It is obvious that some English words will not have a corresponding form in the Persian side, so in these cases, we replace the English constituent leaf with \*NONE\*. In some cases, the personal pronouns acting as subjects (sometimes also objects) are naturally embedded in the verb. In those cases, pronoun in the original tree is replaced with \*NONE\* and its subtree is moved after the verb phrase.

#### Case markers
Persian uses case marker “ra” to denote the syntactic functions of nouns and noun groups. For example, accusative case may be used to mark the direct object of a transitive verb and locative case may be used to mark the head of a prepositional phrase. In English, there are no tokens corresponding to “ra”. When the direct object is definite it is always followed by “ra”; when the direct object is indefinite but individuated it may or may not be followed by “ra” under certain conditions. We add this token after direct object. Also, in translation from English to Persian, the prepositions are sometimes replaced with \*NONE\*. 

#### Plural in nouns and verb inflection
Subject-Verb number agreement is optional in Persian. In translation, we used the case that sounds more natural. Also, plural nouns under NNS tag in the English tree are sometimes translated as singular.

#### Tense and Auxiliary Verb ambiguity
In general, tenses in English are different than tenses in Persian. We translate English tenses to corresponding tenses in Persian. If there is no corresponding tense in Persian, we translate in to the closest semantic class in Persian.

When a compound verb comes with an auxiliary verb, another difficult case arises. In that case, auxiliary verb comes between head of compound verb and light verb. Al- though auxiliary verb(s) have corresponding verb(s) in Persian, we did not translate them and bring them under VB with the main verb.

#### WH- Questions
There are significant differences between English question sentences and Persian question sentences. For this case, we benefit from Penn Treebank II annotation where the movement leaves a trace and is associated with wh-constituent with a numeric marker. For example, “WHNP-1” and “\*T\*-1” are associated.

During translation, wh-constituent is replaced with \*NONE\* and its trace is replaced with related question pronoun in Persian.

For Developers
============

## Requirements

* [Java Development Kit 8 or higher](#java), Open JDK or Oracle JDK
* [Maven](#maven)
* [Git](#git)

### Java 

To check if you have a compatible version of Java installed, use the following command:

    java -version
    
If you don't have a compatible version, you can download either [Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or [OpenJDK](https://openjdk.java.net/install/)    

### Maven
To check if you have Maven installed, use the following command:

    mvn --version
    
To install Maven, you can follow the instructions [here](https://maven.apache.org/install.html).      

### Git

Install the [latest version of Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

## Download Code

In order to work on code, create a fork from GitHub page. 
Use Git for cloning the code to your local or below line for Ubuntu:

	git clone <your-fork-git-link>

A directory called WordNet will be created. Or you can use below link for exploring the code:

	git clone https://github.com/olcaytaner/DataCollector.git

## Open project with IntelliJ IDEA

Steps for opening the cloned project:

* Start IDE
* Select **File | Open** from main menu
* Choose `DataCollector/pom.xml` file
* Select open as project option
* Couple of seconds, dependencies with Maven will be downloaded. 


## Compile

**From IDE**

After being done with the downloading and Maven indexing, select **Build Project** option from **Build** menu. After compilation process, user can run Data Collector.

**From Console**

Go to `DataCollector` directory and compile with 

     mvn compile 

## Generating jar files

**From IDE**

Use `package` of 'Lifecycle' from maven window on the right and from `DataCollector` root module.

**From Console**

Use below line to generate jar file:

     mvn install



------------------------------------------------

WordNet
============
+ [Maven Usage](#maven-usage)


### Maven Usage

	<dependency>
  	<groupId>NlpToolkit</groupId>
  	<artifactId>DataCollector</artifactId>
  	<version>1.0.8</version>
	</dependency>

## Cite
If you use this resource on your research, please cite the following paper: 

```
@article{acikgoz,
  title={All-words word sense disambiguation for {T}urkish},
  author={O. Açıkg{\"o}z and A. T. G{\"u}rkan and B. Ertopçu and O. Topsakal and B. {\"O}zenç and A. B. Kanburoğlu and {\.{I}}. Çam and B. Avar and G. Ercan and O. T. Y{\i}ld{\i}z},
  journal={2017 International Conference on Computer Science and Engineering (UBMK)},
  year={2017},
  pages={490-495}
}

@article{akcakaya,
  title={An all-words sense annotated {T}urkish corpus},
  author={S. Akçakaya and O. T. Y{\i}ld{\i}z},
  journal={2018 2nd International Conference on Natural Language and Speech Processing (ICNLSP)},
  year={2018},
  pages={1-6}
}

@inproceedings{arican,
  title={{E}nglish-{T}urkish Parallel Semantic Annotation of Penn-Treebank},
  author={ B. N. Ar{\i}can and {\"O}. Bakay and B. Avar and O. T. Y{\i}ld{\i}z and {\"O}. Ergelen},
  booktitle={Wordnet Conference},
  pages={298},
  year={2019}
}

@inproceedings{ertopcu17,  
author={B. {Ertopçu} and A. B. {Kanburoğlu} and O. {Topsakal} and O. {Açıkgöz} and A. T. {Gürkan} and B. {Özenç} and İ. {Çam} and B. {Avar} and G. {Ercan} and O. T. {Yıldız}},  
booktitle={2017 International Conference on Computer Science and Engineering (UBMK)},  title={A new approach for named entity recognition},   
year={2017},  
pages={474-479}
}

@INPROCEEDINGS{topsakal17,
author={O. {Topsakal} and O. {Açıkgöz} and A. T. {Gürkan} and A. B. {Kanburoğlu} and B. {Ertopçu} and B. {Özenç} and İ. {Çam} and B. {Avar} and G. {Ercan} and O. T. {Yıldız}}, 
booktitle={2017 International Conference on Computer Science and Engineering (UBMK)}, 
title={Shallow parsing in Turkish}, 
year={2017}, 
pages={480-485}
}
