(ns tailrecursion.boot-useful
  (:require
   [clojure.java.io    :as io]
   [boot.core          :refer :all]
   [boot.task.built-in :refer :all]
   [boot.git           :refer [last-commit]]))

(def +VERSION+
  (do (require 'boot.user)
      @(resolve 'boot.user/+VERSION+)))

(def +GPG-CONFIG+
  (let [f (io/file "gpg.edn")]
    (when (.exists f) (read-string (slurp f)))))

(set-env!
  :src-paths    #{"src"}
  :repositories #(conj %
                   ["deploy-clojars"
                    {:url      "https://clojars.org/repo"
                     :username (System/getenv "CLOJARS_USER")
                     :password (System/getenv "CLOJARS_PASS")}]))

(task-options! push [:repo           "deploy"
                     :ensure-branch  "master"
                     :ensure-clean   true
                     :ensure-version +VERSION+
                     :ensure-tag     (last-commit)])

(deftask push-snapshot
  "Deploy snapshot version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (push
    :file            file
    :ensure-snapshot true))

(deftask push-release
  "Deploy release version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (push
    :file           file
    :tag            true
    :gpg-sign       true
    :gpg-keyring    (:keyring +GPG-CONFIG+)
    :gpg-user-id    (:user-id +GPG-CONFIG+)
    :ensure-release true))
