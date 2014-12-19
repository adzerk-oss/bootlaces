(ns adzerk.bootlaces
  {:boot/export-tasks true}
  (:require
   [clojure.java.io    :as io]
   [boot.core          :refer :all]
   [boot.task.built-in :refer :all]
   [boot.git           :refer [last-commit]]))

(def ^:private +gpg-config+
  (let [f (io/file "gpg.edn")]
    (when (.exists f) (read-string (slurp f)))))

(defn bootlaces!
  [version]
  (set-env! :resource-paths #(conj % "src"))
  (task-options! push {:repo           "deploy-clojars"
                       :ensure-branch  "master"
                       :ensure-clean   true
                       :ensure-version version
                       :ensure-tag     (last-commit)}))

(defn- get-creds []
  (mapv #(System/getenv %) ["CLOJARS_USER" "CLOJARS_PASS"]))

(deftask ^:private collect-clojars-credentials
  "Collect CLOJARS_USER and CLOJARS_PASS from the user if they're not set."
  []
  (fn [next-handler]
    (fn [fileset]
      (let [[user pass] (get-creds), clojars-creds (atom {})]
        (if (and user pass)
          (swap! clojars-creds assoc :username user :password pass)
          (do (println "CLOJARS_USER and CLOJARS_PASS were not set; please enter your Clojars credentials.")
              (print "Username: ")
              (#(swap! clojars-creds assoc :username %) (read-line))
              (print "Password: ")
              (#(swap! clojars-creds assoc :password %)
               (apply str (.readPassword (System/console))))))
        (set-env! :repositories #(conj % ["deploy" (merge @clojars-creds {:url "https://clojars.org/repo"})]))
        (next-handler fileset)))))

(deftask build-jar
  "Build jar and install to local repo."
  []
  (comp (pom) (jar) (install)))

(deftask push-snapshot
  "Deploy snapshot version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (comp (collect-clojars-credentials)
        (push :file file :ensure-snapshot true)))

(deftask push-release
  "Deploy release version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (comp
   (collect-clojars-credentials)
   (push
    :file           file
    :tag            true
    :gpg-sign       true
    :gpg-keyring    (:keyring +gpg-config+)
    :gpg-user-id    (:user-id +gpg-config+)
    :ensure-release true)))
