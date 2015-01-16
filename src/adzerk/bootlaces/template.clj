(ns adzerk.bootlaces.template
  (:require [clojure.string :as string]))

(defn pattern-for
  [tag]
  (re-pattern (str "(?ms)(\\[\\]\\(\\Q" tag "\\E\\))(.*?)(\\[\\]\\(/\\Q" tag "\\E\\)\n)")))

(defn replace-tag
  [s tag body]
  (let [matches (re-seq (pattern-for tag) body)]
    (-> (fn [s [elem opentag tagbody closetag]]
          (.replace s elem (str opentag body closetag)))
        (reduce s (re-seq (pattern-for tag) s)))))

(defn update-dependency
  [s project version]
  (let [fmt "\n```clojure\n[%s \"%s\"] ;; latest release\n```\n"]
    (replace-tag s "dependency" (format fmt project version))))

(comment
  
  (def s (slurp "README.md"))
  

  (->> (re-seq (p "dependency") (slurp "README.md")) first (map #(do (println "---") (print %))))

  (-> "README.md"
      slurp
      (update-dependency 'foo/bar "1.2.3")
      println)
  )
