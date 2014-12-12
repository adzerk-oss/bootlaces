(ns adzerk.bootlaces
  {:boot/export-tasks true}
  (:require
   [clojure.java.io    :as io]
   [boot.core          :refer :all]
   [boot.task.built-in :refer :all]
   [boot.git           :refer [last-commit]]))

(def +gpg-config+
  (let [f (io/file "gpg.edn")]
    (when (.exists f) (read-string (slurp f)))))

(defn bootlaces!
  [version]
  (set-env!
    :resource-paths #{"src"}
    :repositories   #(conj % ["deploy-clojars"
                              {:url      "https://clojars.org/repo"
                               :username (System/getenv "CLOJARS_USER")
                               :password (System/getenv "CLOJARS_PASS")}]))

  (task-options! push {:repo           "deploy-clojars"
                       :ensure-branch  "master"
                       :ensure-clean   true
                       :ensure-version version
                       :ensure-tag     (last-commit)}))

(deftask build-jar
  "Build jar and install to local repo."
  []
  (comp (pom) (jar) (install)))

(deftask push-snapshot
  "Deploy snapshot version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (push :file file :ensure-snapshot true))

(deftask push-release
  "Deploy release version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (push
    :file           file
    :tag            true
    :gpg-sign       true
    :gpg-keyring    (:keyring +gpg-config+)
    :gpg-user-id    (:user-id +gpg-config+)
    :ensure-release true))
