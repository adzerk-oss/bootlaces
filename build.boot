(set-env!
  :src-paths    #{"src"}
  :repositories #(conj % ["deploy" {:url      "https://clojars.org/repo"
                                    :username (System/getenv "CLOJARS_USER")
                                    :password (System/getenv "CLOJARS_PASS")}])
  :dependencies '[[org.clojure/clojure       "1.6.0"      :scope "provided"]
                  [boot/core                 "2.0.0-pre5" :scope "provided"]])

(require
  '[clojure.java.io :as io]
  '[boot.git :refer [last-commit]])

(def +VERSION+ "0.1.0-SNAPSHOT")

(def +GPG-CONFIG+
  (let [f (io/file "gpg.edn")]
    (when (.exists f) (read-string (slurp f)))))

(task-options!
  push [:repo           "deploy"
        :ensure-branch  "master"
        :ensure-clean   true
        :ensure-tag     (last-commit)
        :ensure-version +VERSION+]
  pom  [:project        'tailrecursion/boot-useful
        :version        +VERSION+
        :description    "Micha's assorted boot configurations."
        :url            "https://github.com/tailrecursion/boot-useful"
        :scm            {:url "https://github.com/tailrecursion/boot-useful"}
        :license        {:name "Eclipse Public License"
                         :url  "http://www.eclipse.org/legal/epl-v10.html"}])

(deftask build
  "Build jar and install to local repo."
  []
  (comp (pom) (add-src) (jar) (install)))

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
