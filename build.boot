(set-env! :resource-paths #{"src"})

(require '[boot.git :refer [last-commit]]
         '[adzerk.bootlaces :refer :all])

(def +version+ "0.2.0")

(bootlaces! +version+)

(task-options!
  push {:repo           "deploy"
        :ensure-branch  "master"
        :ensure-clean   true
        :ensure-tag     (last-commit)
        :ensure-version +version+}
  pom  {:project        'adzerk/bootlaces
        :version        +version+
        :description    "Adzerk's boot configurations for Clojure libraries "
        :url            "https://github.com/adzerk/bootlaces"
        :scm            {:url "https://github.com/adzerk/bootlaces"}
        :license        {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask deploy
  []
  (comp (build-jar) (push-release)))
