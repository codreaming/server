import logging

import numpy as np

import gensim
import nltk

from gensim.models import Word2Vec, KeyedVectors
from nltk.corpus import stopwords

class Doc2Vec:
    def __init__(self, modelPath):
        self.word2vecModel = KeyedVectors.load_word2vec_format(modelPath, binary=True)
        self.word2vecModel.init_sims(replace=True)

    def doc2vec(self, text):
        tokens = self.tokenize_text(text)
        return self.doc2vec_from_tokens(tokens)

    def doc2vec_from_tokens(self, words):
        all_words, mean = set(), []
        
        for word in words:
            if isinstance(word, np.ndarray):
                mean.append(word)
            elif word in self.word2vecModel.vocab:
                #print wv.syn0norm[wv.vocab[word].index]
                mean.append(self.word2vecModel.syn0norm[self.word2vecModel.vocab[word].index])
                all_words.add(self.word2vecModel.vocab[word].index)

        if not mean:
            logging.warning("cannot compute similarity with no input %s", words)
            # FIXME: remove these examples in pre-processing
            return np.zeros(self.word2vecModel.layer_size,)

        mean = gensim.matutils.unitvec(np.array(mean).mean(axis=0)).astype(np.float32)
        return mean

    def tokenize_text(self, text):
        tokens = []
        for sent in nltk.sent_tokenize(text, language='english'):
            for word in nltk.word_tokenize(sent, language='english'):
                if len(word) < 2:
                    continue
                if word in stopwords.words('english'):
                    continue
                tokens.append(word)
        return tokens
    
