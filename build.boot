(set-env!
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.6.0"     :scope "provided"]
                  [boot/core           "2.0.0-rc2" :scope "provided"]])

(require
  '[clojure.java.io :as io]
  '[boot.git :refer [last-commit]])

(def +version+ "0.1.6")

(task-options!
  push {:repo           "deploy"
        :ensure-branch  "master"
        :ensure-clean   true
        :ensure-tag     (last-commit)
        :ensure-version +version+}
  pom  {:project        'adzerk/bootlaces
        :version        +version+
        :description    "Micha's assorted boot configurations and things."
        :url            "https://github.com/adzerk/bootlaces"
        :scm            {:url "https://github.com/adzerk/bootlaces"}
        :license        {:name "Eclipse Public License"
                         :url  "http://www.eclipse.org/legal/epl-v10.html"}})

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
  (comp
   (collect-clojars-credentials)
   (push
    :file            file
    :ensure-snapshot true)))

(deftask push-release
  "Deploy release version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (comp
   (collect-clojars-credentials)
   (push
    :file           file
    :tag            true
    :gpg-sign       true
    :ensure-release true)))
