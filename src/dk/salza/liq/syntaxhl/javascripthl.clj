(ns dk.salza.liq.syntaxhl.javascripthl
  (:use [dk.salza.liq.slider :as slider :exclude [create]]))

(defn next-face
  [sl face]
  (let [ch (get-char sl)
        pch (-> sl left get-char)
        ppch (-> sl (left 2) get-char)] 
    (cond (= face :stringst) :string
          (= face :string)  (cond (and (= pch "'") (= ppch "\\")) face
                                  (= pch "'") :plain
                                  :else face)
          (= face :plain)   (cond (and (= ch "'") (not= (get-char (right sl)) "\n")) :stringst
                                  (and (= ch "/") (= (-> sl (right 1) (get-char)) "/")) :comment
                                  (and (= pch "\n") (= ch " ") (= (-> sl (right 1) (get-char)) "*")) :comment
                                  (and (= pch "\n") (= ch "/") (= (-> sl (right 1) (get-char)) "*")) :comment
                                  (and (= pch "") (= ch "/") (= (-> sl (right 1) (get-char)) "*")) :comment
                                  (and (or (= pch " ") (= pch "\n") (= pch "")) (re-find #"^(var|function|const|let)[ \(]" (str (string-ahead sl 9) "    "))) :type1
                                  ;(and (= ch ":") (re-matches #"[\( \[{\n]" pch)) :type3
                                  :else face)
          (= face :type1)   (cond (= pch " ") :type2
                                  (= ch "(") :plain
                                  :else face)
          (= face :type2)   (cond (or (= ch " ") (= ch "(") (= ch "=")) :plain
                                  :else face)
  ;        (= face :type3)   (cond (re-matches #"[\)\]}\s]" (or ch " ")) :plain
  ;                                :else face)
          (= face :comment) (cond (= ch "\n") :plain
                                  :else face)
          :else face)))