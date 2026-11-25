<template>
  <div class="p-6 lg:p-8 bg-slate-50/60 min-h-screen">
    <div class="max-w-7xl mx-auto space-y-6">
      
      <!-- 1. TOP STATISTICS HEADER -->
      <div class="flex flex-col xl:flex-row xl:items-center xl:justify-between gap-6 pb-2">
        <div>
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-50 border border-cyan-200/80 text-cyan-800 text-xs font-bold uppercase tracking-wider mb-2">
            <span class="h-1.5 w-1.5 rounded-full bg-[#008795]"></span>
            Publications &amp; Reports Administration
          </div>
          <h1 class="text-3xl lg:text-4xl font-extrabold text-slate-900 tracking-tight">
            Manage Publications &amp; Reports
          </h1>
          <p class="text-slate-500 mt-1 text-sm">
            Publish institutional action plans, annual performance reports, and policy frameworks with multi-language attachments.
          </p>
        </div>

        <!-- 3 KPI Metric Cards -->
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-3.5 w-full xl:w-auto">
          <!-- KPI 1: Total Documents -->
          <div class="flex items-center gap-3.5 px-4 py-3 bg-white rounded-2xl border border-slate-200/80 shadow-sm hover:shadow transition-shadow">
            <div class="w-11 h-11 rounded-xl bg-cyan-50 border border-cyan-100 flex items-center justify-center text-[#008795] shrink-0">
              <i class="fas fa-file-contract text-base"></i>
            </div>
            <div>
              <p class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Total Documents</p>
              <div class="flex items-baseline gap-1.5 mt-0.5">
                <span class="text-xl font-extrabold text-slate-900">{{ publications.length }}</span>
                <span class="text-[11px] font-bold text-emerald-600">({{ activeCount }} Active)</span>
              </div>
            </div>
          </div>

          <!-- KPI 2: Action Plans vs Reports -->
          <div class="flex items-center gap-3.5 px-4 py-3 bg-white rounded-2xl border border-slate-200/80 shadow-sm hover:shadow transition-shadow">
            <div class="w-11 h-11 rounded-xl bg-amber-50 border border-amber-100 flex items-center justify-center text-amber-600 shrink-0">
              <i class="fas fa-chart-line text-base"></i>
            </div>
            <div>
              <p class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Distribution</p>
              <div class="flex items-baseline gap-1.5 mt-0.5">
                <span class="text-xs font-bold text-teal-700">{{ actionPlansCount }} Plans</span>
                <span class="text-slate-300">•</span>
                <span class="text-xs font-bold text-amber-700">{{ annualReportsCount }} Reports</span>
              </div>
            </div>
          </div>

          <!-- KPI 3: Total Storage -->
          <div class="flex items-center gap-3.5 px-4 py-3 bg-white rounded-2xl border border-slate-200/80 shadow-sm hover:shadow transition-shadow">
            <div class="w-11 h-11 rounded-xl bg-emerald-50 border border-emerald-100 flex items-center justify-center text-emerald-600 shrink-0">
              <i class="fas fa-database text-base"></i>
            </div>
            <div>
              <p class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Total Storage</p>
              <div class="flex items-baseline gap-1.5 mt-0.5">
                <span class="text-xl font-extrabold text-slate-900">{{ totalStorage }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Alerts -->
      <div v-if="successMessage" class="p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm font-semibold flex items-center justify-between shadow-sm">
        <div class="flex items-center gap-2.5">
          <svg class="w-5 h-5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          <span>{{ successMessage }}</span>
        </div>
        <button @click="successMessage = ''" class="text-emerald-500 hover:text-emerald-700">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
        </button>
      </div>

      <div v-if="errorMessage" class="p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-sm font-semibold flex items-center justify-between shadow-sm">
        <div class="flex items-center gap-2.5">
          <svg class="w-5 h-5 text-rose-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          <span>{{ errorMessage }}</span>
        </div>
        <button @click="errorMessage = ''" class="text-rose-500 hover:text-rose-700">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
        </button>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        <!-- 2. UPLOAD FORM CARD (5 cols) -->
        <div class="lg:col-span-5 bg-white rounded-3xl border border-slate-200/80 p-6 lg:p-7 shadow-sm">
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 rounded-xl bg-cyan-50 border border-cyan-100 flex items-center justify-center text-[#008795]">
              <i class="fas fa-cloud-upload-alt text-lg"></i>
            </div>
            <div>
              <h2 class="text-lg font-bold text-slate-800">Upload Publication</h2>
              <p class="text-xs text-slate-400">Add institutional document with multi-language attachments</p>
            </div>
          </div>

          <form @submit.prevent="handleSubmit" class="space-y-4">
            <!-- 1. Document Category -->
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Document Category <span class="text-rose-500">*</span>
              </label>
              <div class="grid grid-cols-3 gap-2">
                <button
                  type="button"
                  @click="form.documentType = 'ACTION_PLAN'"
                  class="py-2.5 px-3 rounded-xl border text-xs font-bold flex flex-col items-center justify-center gap-1 transition-all"
                  :class="form.documentType === 'ACTION_PLAN' 
                    ? 'border-[#008795] bg-teal-50 text-[#008795] shadow-xs' 
                    : 'border-slate-200 bg-slate-50 text-slate-600 hover:border-slate-300'"
                >
                  <i class="fas fa-tasks text-sm"></i>
                  <span>Action Plan</span>
                </button>
                <button
                  type="button"
                  @click="form.documentType = 'ANNUAL_REPORT'"
                  class="py-2.5 px-3 rounded-xl border text-xs font-bold flex flex-col items-center justify-center gap-1 transition-all"
                  :class="form.documentType === 'ANNUAL_REPORT' 
                    ? 'border-amber-500 bg-amber-50 text-amber-800 shadow-xs' 
                    : 'border-slate-200 bg-slate-50 text-slate-600 hover:border-slate-300'"
                >
                  <i class="fas fa-file-invoice text-sm"></i>
                  <span>Annual Report</span>
                </button>
                <button
                  type="button"
                  @click="form.documentType = 'CIRCULAR'"
                  class="py-2.5 px-3 rounded-xl border text-xs font-bold flex flex-col items-center justify-center gap-1 transition-all"
                  :class="form.documentType === 'CIRCULAR' 
                    ? 'border-indigo-500 bg-indigo-50 text-indigo-800 shadow-xs' 
                    : 'border-slate-200 bg-slate-50 text-slate-600 hover:border-slate-300'"
                >
                  <i class="fas fa-scroll text-sm"></i>
                  <span>Circular / Policy</span>
                </button>
              </div>
            </div>

            <!-- 2. Title with Leading Icon -->
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Publication Title <span class="text-rose-500">*</span>
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <i class="fas fa-heading text-xs"></i>
                </div>
                <input
                  v-model="form.title"
                  type="text"
                  required
                  :placeholder="form.documentType === 'ANNUAL_REPORT' ? 'e.g. Annual Performance Report 2025' : 'e.g. Department Action Plan 2026'"
                  class="w-full pl-10 pr-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl hover:border-[#008795] hover:bg-teal-50/20 focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition-all duration-200 text-slate-800"
                />
              </div>
            </div>

            <!-- 3. Year & Release Date -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                  Fiscal Year <span class="text-rose-500">*</span>
                </label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <i class="fas fa-calendar text-xs"></i>
                  </div>
                  <input
                    v-model="form.year"
                    type="text"
                    required
                    placeholder="e.g. 2026"
                    class="w-full pl-10 pr-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl hover:border-[#008795] hover:bg-teal-50/20 focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition text-slate-800"
                  />
                </div>
              </div>
              <div>
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                  Release Date <span class="text-rose-500">*</span>
                </label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <i class="fas fa-calendar-day text-xs"></i>
                  </div>
                  <input
                    v-model="form.releaseDate"
                    type="date"
                    required
                    class="w-full pl-10 pr-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl hover:border-[#008795] hover:bg-teal-50/20 focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition text-slate-800"
                  />
                </div>
              </div>
            </div>

            <!-- 4. Description -->
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Description <span class="text-slate-400 font-normal">(Optional)</span>
              </label>
              <textarea
                v-model="form.description"
                rows="3"
                placeholder="Institutional overview, departmental goals, and key performance indicators..."
                class="w-full px-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl hover:border-[#008795] hover:bg-teal-50/20 focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition-all duration-200 text-slate-800 leading-relaxed"
              ></textarea>
            </div>

            <!-- 5. Optional Cover Image -->
            <div>
              <div class="flex items-center justify-between mb-1.5">
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                  Cover Image <span class="text-slate-400 font-normal">(Optional)</span>
                </label>
                <span class="text-[11px] text-slate-400">JPG, PNG, WebP</span>
              </div>
              
              <div 
                v-if="!selectedCoverImage"
                class="border border-dashed border-slate-300 rounded-2xl p-3 text-center hover:border-[#008795] hover:bg-teal-50/10 cursor-pointer transition-all"
                @click="$refs.coverInput.click()"
              >
                <input
                  ref="coverInput"
                  type="file"
                  accept="image/png,image/jpeg,image/webp"
                  class="hidden"
                  @change="handleCoverSelect"
                />
                <div class="flex items-center justify-center gap-2 py-1 text-slate-500">
                  <i class="far fa-image text-slate-400 text-base"></i>
                  <span class="text-xs font-medium">Upload Card Thumbnail</span>
                  <span class="text-[10px] text-slate-400">(Default branded cover used if empty)</span>
                </div>
              </div>

              <!-- Cover Preview -->
              <div v-else class="flex items-center justify-between p-2.5 bg-slate-50 border border-slate-200 rounded-xl">
                <div class="flex items-center gap-3 overflow-hidden">
                  <img :src="coverPreviewUrl" alt="Cover preview" class="w-10 h-10 object-cover rounded-lg border border-slate-200 shrink-0" />
                  <div class="truncate">
                    <p class="text-xs font-bold text-slate-800 truncate">{{ selectedCoverImage.name }}</p>
                    <p class="text-[10px] text-slate-400">{{ formatBytes(selectedCoverImage.size) }}</p>
                  </div>
                </div>
                <button
                  type="button"
                  @click="removeCoverImage"
                  class="p-1.5 text-slate-400 hover:text-rose-600 rounded-lg hover:bg-rose-50"
                  title="Remove cover image"
                >
                  <i class="fas fa-times text-xs"></i>
                </button>
              </div>
            </div>

            <!-- 6. Multi-Language PDF Uploads (At least 1 required) -->
            <div class="space-y-2 pt-1">
              <div class="flex items-center justify-between">
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                  Language PDF Attachments <span class="text-rose-500">*</span>
                </label>
                <span class="text-[11px] text-slate-400">At least 1 required</span>
              </div>

              <!-- English PDF Slot -->
              <div class="p-3 bg-slate-50/90 border border-slate-200 rounded-2xl space-y-2">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <span class="px-2 py-0.5 text-[10px] font-extrabold rounded bg-blue-100 text-blue-800 border border-blue-200">EN</span>
                    <span class="text-xs font-bold text-slate-700">English PDF</span>
                  </div>
                  <span v-if="selectedEnglishFile" class="text-[11px] font-semibold text-emerald-600 flex items-center gap-1">
                    <i class="fas fa-check-circle"></i> Ready
                  </span>
                  <span v-else class="text-[11px] text-slate-400">Optional</span>
                </div>

                <div v-if="!selectedEnglishFile">
                  <input
                    ref="englishInput"
                    type="file"
                    accept=".pdf,application/pdf"
                    class="hidden"
                    @change="e => handleLangFileSelect(e, 'en')"
                  />
                  <button
                    type="button"
                    @click="$refs.englishInput.click()"
                    class="w-full py-2 px-3 border border-dashed border-slate-300 hover:border-blue-400 rounded-xl bg-white text-xs font-medium text-slate-600 hover:text-blue-600 flex items-center justify-center gap-2 transition-all cursor-pointer"
                  >
                    <i class="fas fa-cloud-upload-alt text-xs text-blue-500"></i>
                    <span>Select English PDF</span>
                  </button>
                </div>
                <div v-else class="flex items-center justify-between p-2 bg-white rounded-xl border border-blue-200 shadow-2xs">
                  <div class="flex items-center gap-2 truncate">
                    <i class="fas fa-file-pdf text-rose-500 text-sm"></i>
                    <span class="text-xs font-medium text-slate-800 truncate">{{ selectedEnglishFile.name }}</span>
                    <span class="text-[10px] text-slate-400 shrink-0">({{ formatBytes(selectedEnglishFile.size) }})</span>
                  </div>
                  <button type="button" @click="selectedEnglishFile = null" class="text-slate-400 hover:text-rose-600 p-1">
                    <i class="fas fa-times text-xs"></i>
                  </button>
                </div>
              </div>

              <!-- Sinhala PDF Slot -->
              <div class="p-3 bg-slate-50/90 border border-slate-200 rounded-2xl space-y-2">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <span class="px-2 py-0.5 text-[10px] font-extrabold rounded bg-emerald-100 text-emerald-800 border border-emerald-200">SI</span>
                    <span class="text-xs font-bold text-slate-700">Sinhala PDF (සිංහල)</span>
                  </div>
                  <span v-if="selectedSinhalaFile" class="text-[11px] font-semibold text-emerald-600 flex items-center gap-1">
                    <i class="fas fa-check-circle"></i> Ready
                  </span>
                  <span v-else class="text-[11px] text-slate-400">Optional</span>
                </div>

                <div v-if="!selectedSinhalaFile">
                  <input
                    ref="sinhalaInput"
                    type="file"
                    accept=".pdf,application/pdf"
                    class="hidden"
                    @change="e => handleLangFileSelect(e, 'si')"
                  />
                  <button
                    type="button"
                    @click="$refs.sinhalaInput.click()"
                    class="w-full py-2 px-3 border border-dashed border-slate-300 hover:border-emerald-400 rounded-xl bg-white text-xs font-medium text-slate-600 hover:text-emerald-700 flex items-center justify-center gap-2 transition-all cursor-pointer"
                  >
                    <i class="fas fa-cloud-upload-alt text-xs text-emerald-600"></i>
                    <span>Select Sinhala PDF</span>
                  </button>
                </div>
                <div v-else class="flex items-center justify-between p-2 bg-white rounded-xl border border-emerald-200 shadow-2xs">
                  <div class="flex items-center gap-2 truncate">
                    <i class="fas fa-file-pdf text-rose-500 text-sm"></i>
                    <span class="text-xs font-medium text-slate-800 truncate">{{ selectedSinhalaFile.name }}</span>
                    <span class="text-[10px] text-slate-400 shrink-0">({{ formatBytes(selectedSinhalaFile.size) }})</span>
                  </div>
                  <button type="button" @click="selectedSinhalaFile = null" class="text-slate-400 hover:text-rose-600 p-1">
                    <i class="fas fa-times text-xs"></i>
                  </button>
                </div>
              </div>

              <!-- Tamil PDF Slot -->
              <div class="p-3 bg-slate-50/90 border border-slate-200 rounded-2xl space-y-2">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <span class="px-2 py-0.5 text-[10px] font-extrabold rounded bg-purple-100 text-purple-800 border border-purple-200">TA</span>
                    <span class="text-xs font-bold text-slate-700">Tamil PDF (தமிழ்)</span>
                  </div>
                  <span v-if="selectedTamilFile" class="text-[11px] font-semibold text-emerald-600 flex items-center gap-1">
                    <i class="fas fa-check-circle"></i> Ready
                  </span>
                  <span v-else class="text-[11px] text-slate-400">Optional</span>
                </div>

                <div v-if="!selectedTamilFile">
                  <input
                    ref="tamilInput"
                    type="file"
                    accept=".pdf,application/pdf"
                    class="hidden"
                    @change="e => handleLangFileSelect(e, 'ta')"
                  />
                  <button
                    type="button"
                    @click="$refs.tamilInput.click()"
                    class="w-full py-2 px-3 border border-dashed border-slate-300 hover:border-purple-400 rounded-xl bg-white text-xs font-medium text-slate-600 hover:text-purple-700 flex items-center justify-center gap-2 transition-all cursor-pointer"
                  >
                    <i class="fas fa-cloud-upload-alt text-xs text-purple-600"></i>
                    <span>Select Tamil PDF</span>
                  </button>
                </div>
                <div v-else class="flex items-center justify-between p-2 bg-white rounded-xl border border-purple-200 shadow-2xs">
                  <div class="flex items-center gap-2 truncate">
                    <i class="fas fa-file-pdf text-rose-500 text-sm"></i>
                    <span class="text-xs font-medium text-slate-800 truncate">{{ selectedTamilFile.name }}</span>
                    <span class="text-[10px] text-slate-400 shrink-0">({{ formatBytes(selectedTamilFile.size) }})</span>
                  </div>
                  <button type="button" @click="selectedTamilFile = null" class="text-slate-400 hover:text-rose-600 p-1">
                    <i class="fas fa-times text-xs"></i>
                  </button>
                </div>
              </div>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              :disabled="isSubmitting"
              class="w-full py-3.5 px-6 rounded-2xl bg-gradient-to-r from-[#008795] to-[#006071] hover:from-[#009fb0] hover:to-[#007487] text-white text-sm font-bold shadow-md shadow-cyan-900/15 hover:shadow-xl hover:shadow-cyan-900/25 hover:-translate-y-0.5 active:translate-y-0 transition-all duration-200 flex items-center justify-center gap-2.5 disabled:opacity-60 disabled:cursor-not-allowed cursor-pointer"
            >
              <i v-if="isSubmitting" class="fas fa-circle-notch fa-spin text-sm"></i>
              <i v-else class="fas fa-cloud-upload-alt text-sm"></i>
              <span>{{ isSubmitting ? 'Publishing to Database...' : 'Publish Publication' }}</span>
            </button>
          </form>
        </div>

        <!-- 3. DATABASE LEDGER (7 cols) -->
        <div class="lg:col-span-7 bg-white rounded-3xl border border-slate-200/80 p-6 lg:p-7 shadow-sm">
          <div class="flex items-center justify-between mb-5">
            <div>
              <h2 class="text-lg font-bold text-slate-800">Database Ledger</h2>
              <p class="text-xs text-slate-400">Synchronized records grouped by category and year</p>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs font-bold px-2.5 py-1 rounded-full bg-slate-100 text-slate-600">
                {{ filteredPublications.length }} / {{ publications.length }}
              </span>
              <button
                @click="fetchPublications"
                class="p-2 rounded-xl border border-slate-200 text-slate-500 hover:text-slate-800 hover:bg-slate-50 transition-colors cursor-pointer"
                title="Refresh ledger"
              >
                <i class="fas fa-sync-alt text-xs" :class="{ 'fa-spin': isLoadingList }"></i>
              </button>
            </div>
          </div>

          <!-- Category Filter Tabs -->
          <div class="flex items-center gap-2 border-b border-slate-100 pb-3 mb-4 overflow-x-auto no-scrollbar">
            <button
              v-for="cat in categoryFilterTabs"
              :key="cat.value"
              type="button"
              @click="selectedCategoryTab = cat.value"
              class="px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 cursor-pointer flex items-center gap-1.5"
              :class="selectedCategoryTab === cat.value
                ? 'bg-[#008795] text-white shadow-xs'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200/70'"
            >
              <span>{{ cat.label }}</span>
              <span class="px-1.5 py-0.2 rounded-full text-[10px]" :class="selectedCategoryTab === cat.value ? 'bg-white/20 text-white' : 'bg-slate-200 text-slate-500'">
                {{ cat.count }}
              </span>
            </button>
          </div>

          <!-- Search & Year Filters -->
          <div class="space-y-3 mb-5">
            <!-- Search Bar -->
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                <i class="fas fa-search text-xs"></i>
              </div>
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search publications by title, year, or description..."
                class="w-full pl-9 pr-9 py-2.5 text-xs bg-slate-50 border border-slate-200 rounded-xl hover:border-[#008795] hover:bg-teal-50/20 focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition text-slate-800 placeholder-slate-400"
              />
              <button
                v-if="searchQuery"
                type="button"
                @click="searchQuery = ''"
                class="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600"
                title="Clear search"
              >
                <i class="fas fa-times-circle text-xs"></i>
              </button>
            </div>

            <!-- Year Filter Pills -->
            <div class="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar">
              <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider mr-1 shrink-0">Filter Year:</span>
              <button
                v-for="yr in availableYears"
                :key="yr"
                type="button"
                @click="selectedYearFilter = yr"
                class="px-3 py-1 rounded-xl text-xs font-bold transition-all shrink-0 cursor-pointer"
                :class="selectedYearFilter === yr
                  ? 'bg-slate-800 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200/80'"
              >
                {{ yr }}
              </button>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="isLoadingList" class="py-16 text-center text-slate-400">
            <i class="fas fa-circle-notch fa-spin text-2xl text-[#008795] mb-2"></i>
            <p class="text-xs font-medium">Synchronizing publications...</p>
          </div>

          <!-- Empty Database State -->
          <div v-else-if="publications.length === 0" class="py-16 text-center text-slate-400">
            <div class="w-16 h-16 mx-auto rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-300 mb-3">
              <i class="fas fa-folder-open text-2xl"></i>
            </div>
            <p class="text-sm font-semibold text-slate-700">No publications in database</p>
            <p class="text-xs text-slate-400 mt-1">Use the upload form on the left to deploy your first document.</p>
          </div>

          <!-- No Filter Results State -->
          <div v-else-if="filteredPublications.length === 0" class="py-12 text-center text-slate-400">
            <div class="w-12 h-12 mx-auto rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-400 mb-2">
              <i class="fas fa-search text-base"></i>
            </div>
            <p class="text-xs font-semibold text-slate-600">No matching publications found</p>
            <p class="text-[11px] text-slate-400 mt-0.5">Try changing your search query, year filter, or category tab.</p>
            <button
              @click="searchQuery = ''; selectedYearFilter = 'All'; selectedCategoryTab = 'ALL'"
              class="mt-3 px-3 py-1 rounded-lg text-xs font-semibold text-[#008795] hover:bg-cyan-50 transition-colors cursor-pointer"
            >
              Reset Filters
            </button>
          </div>

          <!-- Publication Cards List -->
          <div v-else class="space-y-3.5">
            <div
              v-for="pub in filteredPublications"
              :key="pub.id"
              class="group p-4 rounded-2xl border border-slate-200/80 bg-white hover:bg-slate-50/60 shadow-xs hover:shadow-md transition-all duration-200 space-y-3"
              :class="getCategoryBorderClass(pub.documentType || pub.category)"
            >
              <div class="flex items-start justify-between gap-3">
                <div class="flex items-start gap-3 min-w-0 flex-1">
                  <!-- Thumbnail preview or branded icon box -->
                  <div class="w-11 h-14 rounded-lg overflow-hidden border border-slate-200 shrink-0 bg-slate-100 flex items-center justify-center">
                    <img
                      v-if="pub.coverImageUrl"
                      :src="getMediaUrl(pub.coverImageUrl)"
                      alt="Cover"
                      class="w-full h-full object-cover"
                    />
                    <div v-else class="w-full h-full bg-gradient-to-br from-[#008795] to-[#003b46] flex flex-col items-center justify-center text-white p-1 text-center">
                      <span class="text-[9px] font-extrabold uppercase leading-tight">{{ pub.year }}</span>
                      <i class="fas fa-file-alt text-[10px] opacity-80 mt-0.5"></i>
                    </div>
                  </div>

                  <div class="min-w-0 flex-1">
                    <div class="flex flex-wrap items-center gap-1.5 mb-1">
                      <!-- Category Badge -->
                      <span
                        class="px-2 py-0.5 rounded-md text-[10px] font-extrabold border"
                        :class="getCategoryBadgeClass(pub.documentType || pub.category)"
                      >
                        {{ formatCategoryLabel(pub.documentType || pub.category) }}
                      </span>

                      <!-- Year Pill -->
                      <span class="px-2 py-0.5 rounded-md text-[10px] font-extrabold bg-slate-100 text-slate-700 border border-slate-200">
                        FY {{ pub.year }}
                      </span>

                      <!-- Release Date -->
                      <span class="text-[11px] text-slate-400 font-medium flex items-center gap-1 ml-1">
                        <i class="far fa-calendar-alt text-[10px]"></i>
                        {{ pub.releaseDate || 'N/A' }}
                      </span>
                    </div>

                    <h3 class="text-sm font-bold text-slate-800 group-hover:text-[#008795] transition-colors truncate" :title="pub.title">
                      {{ pub.title }}
                    </h3>

                    <!-- Multi-Language Availability Pills -->
                    <div class="flex items-center gap-1.5 mt-2">
                      <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">PDFs:</span>
                      
                      <!-- English Pill -->
                      <a
                        v-if="pub.pdfEnglishUrl || pub.fileUrl"
                        :href="getMediaUrl(pub.pdfEnglishUrl || pub.fileUrl)"
                        target="_blank"
                        class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-blue-50 text-blue-700 border border-blue-200 hover:bg-blue-100 transition-colors"
                        title="View English PDF"
                      >
                        <i class="fas fa-file-pdf text-[9px] text-blue-600"></i>
                        <span>EN</span>
                        <span class="text-[9px] text-blue-500/80 font-normal">({{ pub.pdfEnglishSize || pub.fileSize || 'PDF' }})</span>
                      </a>

                      <!-- Sinhala Pill -->
                      <a
                        v-if="pub.pdfSinhalaUrl"
                        :href="getMediaUrl(pub.pdfSinhalaUrl)"
                        target="_blank"
                        class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200 hover:bg-emerald-100 transition-colors"
                        title="View Sinhala PDF"
                      >
                        <i class="fas fa-file-pdf text-[9px] text-emerald-600"></i>
                        <span>SI</span>
                        <span class="text-[9px] text-emerald-600/80 font-normal">({{ pub.pdfSinhalaSize || 'PDF' }})</span>
                      </a>

                      <!-- Tamil Pill -->
                      <a
                        v-if="pub.pdfTamilUrl"
                        :href="getMediaUrl(pub.pdfTamilUrl)"
                        target="_blank"
                        class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-bold bg-purple-50 text-purple-700 border border-purple-200 hover:bg-purple-100 transition-colors"
                        title="View Tamil PDF"
                      >
                        <i class="fas fa-file-pdf text-[9px] text-purple-600"></i>
                        <span>TA</span>
                        <span class="text-[9px] text-purple-600/80 font-normal">({{ pub.pdfTamilSize || 'PDF' }})</span>
                      </a>

                      <span v-if="!pub.pdfEnglishUrl && !pub.pdfSinhalaUrl && !pub.pdfTamilUrl && !pub.fileUrl" class="text-[10px] text-slate-400 italic">
                        No files attached
                      </span>
                    </div>
                  </div>
                </div>

                <!-- Actions: Active Pill + Edit, Delete -->
                <div class="flex items-center gap-1.5 shrink-0">
                  <button
                    @click="toggleActive(pub.id)"
                    class="px-2.5 py-1 rounded-full text-[10px] font-extrabold uppercase tracking-wider transition-colors cursor-pointer"
                    :class="pub.active ? 'bg-emerald-100 text-emerald-800 hover:bg-emerald-200' : 'bg-slate-200 text-slate-600 hover:bg-slate-300'"
                    :title="pub.active ? 'Click to deactivate' : 'Click to activate'"
                  >
                    {{ pub.active ? 'Active' : 'Inactive' }}
                  </button>

                  <button
                    @click="openEditModal(pub)"
                    class="w-8 h-8 rounded-full bg-slate-100 hover:bg-cyan-50 text-slate-500 hover:text-[#008795] border border-slate-200/80 flex items-center justify-center transition-all shadow-xs cursor-pointer"
                    title="Edit publication"
                  >
                    <i class="fas fa-pen text-xs"></i>
                  </button>

                  <button
                    @click="openDeleteModal(pub)"
                    class="w-8 h-8 rounded-full bg-slate-100 hover:bg-rose-50 text-slate-500 hover:text-rose-600 border border-slate-200/80 flex items-center justify-center transition-all shadow-xs cursor-pointer"
                    title="Delete publication"
                  >
                    <i class="fas fa-trash-alt text-xs"></i>
                  </button>
                </div>
              </div>

              <p v-if="pub.description" class="text-xs text-slate-500 line-clamp-2 leading-relaxed pl-14">
                {{ pub.description }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. DELETE CONFIRMATION MODAL -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm transition-opacity"
      @click.self="closeDeleteModal"
    >
      <div class="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl border border-slate-100 transform transition-all">
        <div class="w-14 h-14 rounded-2xl bg-amber-50 border border-amber-200/80 flex items-center justify-center text-amber-500 mb-4 mx-auto">
          <i class="fas fa-exclamation-triangle text-2xl"></i>
        </div>

        <h3 class="text-lg font-bold text-slate-900 text-center">
          Delete Publication?
        </h3>
        <p class="text-sm text-slate-500 text-center mt-2 leading-relaxed">
          Are you sure you want to delete <span class="font-bold text-slate-800 break-words">"{{ itemToDelete?.title }}"</span>? This document and its attachments will be permanently removed.
        </p>

        <div class="flex items-center gap-3 mt-6">
          <button
            type="button"
            @click="closeDeleteModal"
            class="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 text-slate-700 hover:bg-slate-50 font-semibold text-sm transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button
            type="button"
            @click="executeDelete"
            :disabled="isDeleting"
            class="flex-1 px-4 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-semibold text-sm shadow-md shadow-rose-600/20 transition-all flex items-center justify-center gap-2 cursor-pointer"
          >
            <i v-if="isDeleting" class="fas fa-spinner fa-spin text-sm"></i>
            <span>{{ isDeleting ? 'Deleting...' : 'Delete' }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 5. EDIT PUBLICATION MODAL -->
    <div
      v-if="showEditModal"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm overflow-y-auto"
      @click.self="closeEditModal"
    >
      <div class="bg-white rounded-3xl max-w-2xl w-full p-6 lg:p-7 shadow-2xl border border-slate-100 my-8 transform transition-all">
        <div class="flex items-center justify-between pb-4 border-b border-slate-100 mb-5">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-cyan-50 border border-cyan-100 flex items-center justify-center text-[#008795]">
              <i class="fas fa-edit text-base"></i>
            </div>
            <div>
              <h3 class="text-lg font-bold text-slate-800">Edit Publication</h3>
              <p class="text-xs text-slate-400">Update metadata and manage language attachments</p>
            </div>
          </div>
          <button
            type="button"
            @click="closeEditModal"
            class="p-2 text-slate-400 hover:text-slate-600 rounded-xl hover:bg-slate-50 transition-colors cursor-pointer"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div v-if="editErrorMessage" class="p-3 mb-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs font-semibold flex items-center justify-between">
          <span>{{ editErrorMessage }}</span>
          <button @click="editErrorMessage = ''" class="text-rose-500 hover:text-rose-700">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        <form @submit.prevent="handleUpdate" class="space-y-4">
          <!-- Category Selector in Edit -->
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Document Category <span class="text-rose-500">*</span></label>
            <div class="grid grid-cols-3 gap-2">
              <button
                type="button"
                @click="editForm.documentType = 'ACTION_PLAN'"
                class="py-2 px-3 rounded-xl border text-xs font-bold flex items-center justify-center gap-1.5 transition-all"
                :class="editForm.documentType === 'ACTION_PLAN' 
                  ? 'border-[#008795] bg-teal-50 text-[#008795]' 
                  : 'border-slate-200 bg-slate-50 text-slate-600 hover:border-slate-300'"
              >
                <i class="fas fa-tasks text-xs"></i>
                <span>Action Plan</span>
              </button>
              <button
                type="button"
                @click="editForm.documentType = 'ANNUAL_REPORT'"
                class="py-2 px-3 rounded-xl border text-xs font-bold flex items-center justify-center gap-1.5 transition-all"
                :class="editForm.documentType === 'ANNUAL_REPORT' 
                  ? 'border-amber-500 bg-amber-50 text-amber-800' 
                  : 'border-slate-200 bg-slate-50 text-slate-600 hover:border-slate-300'"
              >
                <i class="fas fa-file-invoice text-xs"></i>
                <span>Annual Report</span>
              </button>
              <button
                type="button"
                @click="editForm.documentType = 'CIRCULAR'"
                class="py-2 px-3 rounded-xl border text-xs font-bold flex items-center justify-center gap-1.5 transition-all"
                :class="editForm.documentType === 'CIRCULAR' 
                  ? 'border-indigo-500 bg-indigo-50 text-indigo-800' 
                  : 'border-slate-200 bg-slate-50 text-slate-600 hover:border-slate-300'"
              >
                <i class="fas fa-scroll text-xs"></i>
                <span>Circular</span>
              </button>
            </div>
          </div>

          <!-- Title -->
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Publication Title <span class="text-rose-500">*</span></label>
            <input
              v-model="editForm.title"
              type="text"
              required
              class="w-full px-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition text-slate-800"
            />
          </div>

          <!-- Year & Release Date -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Fiscal Year <span class="text-rose-500">*</span></label>
              <input
                v-model="editForm.year"
                type="text"
                required
                class="w-full px-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition text-slate-800"
              />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Release Date <span class="text-rose-500">*</span></label>
              <input
                v-model="editForm.releaseDate"
                type="date"
                required
                class="w-full px-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition text-slate-800"
              />
            </div>
          </div>

          <!-- Description -->
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Description</label>
            <textarea
              v-model="editForm.description"
              rows="3"
              class="w-full px-4 py-2.5 text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-[#008795] focus:border-[#008795] outline-none transition text-slate-800 leading-relaxed"
            ></textarea>
          </div>

          <!-- Cover Image in Edit -->
          <div>
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">Cover Image (Optional)</label>
            <div v-if="editForm.currentCoverImageUrl && !editRemoveCover" class="flex items-center justify-between p-2.5 bg-slate-50 border border-slate-200 rounded-xl mb-2">
              <div class="flex items-center gap-3">
                <img :src="getMediaUrl(editForm.currentCoverImageUrl)" alt="Current cover" class="w-10 h-10 object-cover rounded-lg border border-slate-200 shrink-0" />
                <span class="text-xs font-medium text-slate-700">Existing Cover Image</span>
              </div>
              <button
                type="button"
                @click="editRemoveCover = true"
                class="text-xs font-bold text-rose-600 hover:underline"
              >
                Remove Cover
              </button>
            </div>

            <div v-if="!editCoverFile && (!editForm.currentCoverImageUrl || editRemoveCover)">
              <input
                ref="editCoverInput"
                type="file"
                accept="image/png,image/jpeg,image/webp"
                class="hidden"
                @change="handleEditCoverSelect"
              />
              <button
                type="button"
                @click="$refs.editCoverInput.click()"
                class="w-full py-2 px-3 border border-dashed border-slate-300 hover:border-[#008795] rounded-xl bg-slate-50 text-xs font-medium text-slate-600 hover:text-[#008795] flex items-center justify-center gap-2 transition-all cursor-pointer"
              >
                <i class="far fa-image text-slate-400"></i>
                <span>Upload New Cover Image</span>
              </button>
            </div>
            <div v-else-if="editCoverFile" class="flex items-center justify-between p-2 bg-white rounded-xl border border-teal-200">
              <span class="text-xs font-medium text-slate-800 truncate">{{ editCoverFile.name }} ({{ formatBytes(editCoverFile.size) }})</span>
              <button type="button" @click="editCoverFile = null" class="text-slate-400 hover:text-rose-600 p-1">
                <i class="fas fa-times text-xs"></i>
              </button>
            </div>
          </div>

          <!-- Language Attachments in Edit -->
          <div class="space-y-2 pt-1 border-t border-slate-100">
            <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider pt-2">
              Manage Language PDF Files
            </label>

            <!-- English Edit Slot -->
            <div class="p-3 bg-slate-50 rounded-xl border border-slate-200 space-y-2">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="px-2 py-0.5 text-[10px] font-extrabold rounded bg-blue-100 text-blue-800 border border-blue-200">EN</span>
                  <span class="text-xs font-bold text-slate-700">English PDF</span>
                </div>
                <div v-if="editForm.currentEnglishUrl && !editRemoveEnglish" class="flex items-center gap-2">
                  <a :href="getMediaUrl(editForm.currentEnglishUrl)" target="_blank" class="text-[11px] font-bold text-blue-600 hover:underline">
                    View Current
                  </a>
                  <button type="button" @click="editRemoveEnglish = true" class="text-[11px] font-bold text-rose-500 hover:underline">
                    Remove
                  </button>
                </div>
                <span v-else-if="editRemoveEnglish" class="text-[11px] text-rose-500 italic">Marked for removal</span>
                <span v-else class="text-[11px] text-slate-400">Not uploaded</span>
              </div>

              <div class="flex items-center gap-2">
                <input
                  ref="editEnglishInput"
                  type="file"
                  accept=".pdf,application/pdf"
                  class="hidden"
                  @change="e => handleEditLangFileSelect(e, 'en')"
                />
                <button
                  type="button"
                  @click="$refs.editEnglishInput.click()"
                  class="text-xs font-medium px-3 py-1.5 rounded-lg border border-slate-200 hover:border-blue-400 bg-white text-slate-600 hover:text-blue-600 transition-colors cursor-pointer"
                >
                  <i class="fas fa-cloud-upload-alt mr-1"></i>
                  {{ editForm.currentEnglishUrl && !editRemoveEnglish ? 'Replace English PDF' : 'Upload English PDF' }}
                </button>
                <span v-if="editEnglishFile" class="text-xs font-semibold text-emerald-600 truncate">
                  {{ editEnglishFile.name }} (Ready)
                </span>
              </div>
            </div>

            <!-- Sinhala Edit Slot -->
            <div class="p-3 bg-slate-50 rounded-xl border border-slate-200 space-y-2">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="px-2 py-0.5 text-[10px] font-extrabold rounded bg-emerald-100 text-emerald-800 border border-emerald-200">SI</span>
                  <span class="text-xs font-bold text-slate-700">Sinhala PDF (සිංහල)</span>
                </div>
                <div v-if="editForm.currentSinhalaUrl && !editRemoveSinhala" class="flex items-center gap-2">
                  <a :href="getMediaUrl(editForm.currentSinhalaUrl)" target="_blank" class="text-[11px] font-bold text-emerald-600 hover:underline">
                    View Current
                  </a>
                  <button type="button" @click="editRemoveSinhala = true" class="text-[11px] font-bold text-rose-500 hover:underline">
                    Remove
                  </button>
                </div>
                <span v-else-if="editRemoveSinhala" class="text-[11px] text-rose-500 italic">Marked for removal</span>
                <span v-else class="text-[11px] text-slate-400">Not uploaded</span>
              </div>

              <div class="flex items-center gap-2">
                <input
                  ref="editSinhalaInput"
                  type="file"
                  accept=".pdf,application/pdf"
                  class="hidden"
                  @change="e => handleEditLangFileSelect(e, 'si')"
                />
                <button
                  type="button"
                  @click="$refs.editSinhalaInput.click()"
                  class="text-xs font-medium px-3 py-1.5 rounded-lg border border-slate-200 hover:border-emerald-400 bg-white text-slate-600 hover:text-emerald-700 transition-colors cursor-pointer"
                >
                  <i class="fas fa-cloud-upload-alt mr-1"></i>
                  {{ editForm.currentSinhalaUrl && !editRemoveSinhala ? 'Replace Sinhala PDF' : 'Upload Sinhala PDF' }}
                </button>
                <span v-if="editSinhalaFile" class="text-xs font-semibold text-emerald-600 truncate">
                  {{ editSinhalaFile.name }} (Ready)
                </span>
              </div>
            </div>

            <!-- Tamil Edit Slot -->
            <div class="p-3 bg-slate-50 rounded-xl border border-slate-200 space-y-2">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="px-2 py-0.5 text-[10px] font-extrabold rounded bg-purple-100 text-purple-800 border border-purple-200">TA</span>
                  <span class="text-xs font-bold text-slate-700">Tamil PDF (தமிழ்)</span>
                </div>
                <div v-if="editForm.currentTamilUrl && !editRemoveTamil" class="flex items-center gap-2">
                  <a :href="getMediaUrl(editForm.currentTamilUrl)" target="_blank" class="text-[11px] font-bold text-purple-600 hover:underline">
                    View Current
                  </a>
                  <button type="button" @click="editRemoveTamil = true" class="text-[11px] font-bold text-rose-500 hover:underline">
                    Remove
                  </button>
                </div>
                <span v-else-if="editRemoveTamil" class="text-[11px] text-rose-500 italic">Marked for removal</span>
                <span v-else class="text-[11px] text-slate-400">Not uploaded</span>
              </div>

              <div class="flex items-center gap-2">
                <input
                  ref="editTamilInput"
                  type="file"
                  accept=".pdf,application/pdf"
                  class="hidden"
                  @change="e => handleEditLangFileSelect(e, 'ta')"
                />
                <button
                  type="button"
                  @click="$refs.editTamilInput.click()"
                  class="text-xs font-medium px-3 py-1.5 rounded-lg border border-slate-200 hover:border-purple-400 bg-white text-slate-600 hover:text-purple-700 transition-colors cursor-pointer"
                >
                  <i class="fas fa-cloud-upload-alt mr-1"></i>
                  {{ editForm.currentTamilUrl && !editRemoveTamil ? 'Replace Tamil PDF' : 'Upload Tamil PDF' }}
                </button>
                <span v-if="editTamilFile" class="text-xs font-semibold text-emerald-600 truncate">
                  {{ editTamilFile.name }} (Ready)
                </span>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <button
              type="button"
              @click="closeEditModal"
              class="px-4 py-2.5 rounded-xl border border-slate-200 text-slate-700 hover:bg-slate-50 font-semibold text-xs transition-colors cursor-pointer"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="isUpdating"
              class="px-5 py-2.5 rounded-xl bg-[#008795] hover:bg-[#007480] text-white font-bold text-xs shadow-md transition-all flex items-center gap-2 cursor-pointer"
            >
              <i v-if="isUpdating" class="fas fa-spinner fa-spin text-xs"></i>
              <span>{{ isUpdating ? 'Saving Changes...' : 'Save Changes' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import instance from '@/axiosInstance';

const form = ref({
  documentType: 'ACTION_PLAN',
  title: '',
  year: new Date().getFullYear().toString(),
  releaseDate: new Date().toISOString().split('T')[0],
  description: ''
});

const selectedCoverImage = ref(null);
const coverPreviewUrl = ref('');
const selectedEnglishFile = ref(null);
const selectedSinhalaFile = ref(null);
const selectedTamilFile = ref(null);

const isSubmitting = ref(false);
const isLoadingList = ref(false);
const successMessage = ref('');
const errorMessage = ref('');
const publications = ref([]);

// Search and Filter State
const searchQuery = ref('');
const selectedYearFilter = ref('All');
const selectedCategoryTab = ref('ALL');

// Delete Modal State
const showDeleteModal = ref(false);
const itemToDelete = ref(null);
const isDeleting = ref(false);

// Edit Modal State
const showEditModal = ref(false);
const editForm = ref({
  id: null,
  documentType: 'ACTION_PLAN',
  title: '',
  year: '',
  releaseDate: '',
  description: '',
  currentCoverImageUrl: '',
  currentEnglishUrl: '',
  currentSinhalaUrl: '',
  currentTamilUrl: ''
});
const editCoverFile = ref(null);
const editRemoveCover = ref(false);
const editEnglishFile = ref(null);
const editRemoveEnglish = ref(false);
const editSinhalaFile = ref(null);
const editRemoveSinhala = ref(false);
const editTamilFile = ref(null);
const editRemoveTamil = ref(false);
const isUpdating = ref(false);
const editErrorMessage = ref('');

const activeCount = computed(() => {
  return publications.value.filter(p => p.active !== false).length;
});

const actionPlansCount = computed(() => {
  return publications.value.filter(p => (p.documentType || p.category) === 'ACTION_PLAN').length;
});

const annualReportsCount = computed(() => {
  return publications.value.filter(p => (p.documentType || p.category) === 'ANNUAL_REPORT').length;
});

const categoryFilterTabs = computed(() => {
  const total = publications.value.length;
  const plans = actionPlansCount.value;
  const reports = annualReportsCount.value;
  const circulars = publications.value.filter(p => (p.documentType || p.category) === 'CIRCULAR').length;

  return [
    { label: 'All Documents', value: 'ALL', count: total },
    { label: 'Action Plans', value: 'ACTION_PLAN', count: plans },
    { label: 'Annual Reports', value: 'ANNUAL_REPORT', count: reports },
    { label: 'Circulars', value: 'CIRCULAR', count: circulars },
  ];
});

const availableYears = computed(() => {
  const years = new Set(publications.value.map(p => p.year).filter(Boolean));
  if (years.size === 0) {
    return ['All', '2026', '2025', '2024'];
  }
  const sorted = Array.from(years).sort((a, b) => b.localeCompare(a));
  return ['All', ...sorted];
});

const totalStorage = computed(() => {
  if (publications.value.length === 0) return '0.0 MB';
  let totalBytes = 0;
  publications.value.forEach(p => {
    [p.pdfEnglishSize, p.pdfSinhalaSize, p.pdfTamilSize, p.fileSize].forEach(sizeStr => {
      if (!sizeStr) return;
      const s = sizeStr.toString().trim().toUpperCase();
      const num = parseFloat(s);
      if (isNaN(num)) return;
      if (s.includes('GB')) totalBytes += num * 1024 * 1024 * 1024;
      else if (s.includes('MB')) totalBytes += num * 1024 * 1024;
      else if (s.includes('KB')) totalBytes += num * 1024;
      else if (s.includes('B')) totalBytes += num;
      else totalBytes += num * 1024;
    });
  });
  if (totalBytes === 0) return '0.0 MB';
  return formatBytes(totalBytes);
});

const filteredPublications = computed(() => {
  return publications.value.filter(pub => {
    // Category Tab Filter
    const docCat = (pub.documentType || pub.category || 'ACTION_PLAN').toUpperCase();
    if (selectedCategoryTab.value !== 'ALL' && docCat !== selectedCategoryTab.value) {
      return false;
    }

    // Year Filter
    const matchesYear = selectedYearFilter.value === 'All' || String(pub.year) === selectedYearFilter.value;
    
    // Search Query
    const q = searchQuery.value.toLowerCase().trim();
    const matchesQuery = !q || 
      (pub.title && pub.title.toLowerCase().includes(q)) || 
      (pub.description && pub.description.toLowerCase().includes(q)) ||
      (pub.year && String(pub.year).includes(q)) ||
      (pub.releaseDate && pub.releaseDate.toLowerCase().includes(q));

    return matchesYear && matchesQuery;
  });
});

const formatBytes = (bytes, decimals = 1) => {
  if (!bytes) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};

const formatCategoryLabel = (category) => {
  const cat = (category || 'ACTION_PLAN').toUpperCase();
  if (cat === 'ANNUAL_REPORT') return 'Annual Performance Report';
  if (cat === 'CIRCULAR') return 'Circular / Policy';
  return 'Action Plan';
};

const getCategoryBadgeClass = (category) => {
  const cat = (category || 'ACTION_PLAN').toUpperCase();
  if (cat === 'ANNUAL_REPORT') return 'bg-amber-50 text-amber-800 border-amber-200/80';
  if (cat === 'CIRCULAR') return 'bg-indigo-50 text-indigo-800 border-indigo-200/80';
  return 'bg-teal-50 text-[#008795] border-teal-200/80';
};

const getCategoryBorderClass = (category) => {
  const cat = (category || 'ACTION_PLAN').toUpperCase();
  if (cat === 'ANNUAL_REPORT') return 'border-l-4 border-l-amber-500';
  if (cat === 'CIRCULAR') return 'border-l-4 border-l-indigo-500';
  return 'border-l-4 border-l-[#008795]';
};

const formatReleaseDateForInput = (dateStr) => {
  if (!dateStr) return '';
  if (/^\d{4}-\d{2}-\d{2}$/.test(dateStr)) return dateStr;
  const d = new Date(dateStr);
  if (!isNaN(d.getTime())) {
    return d.toISOString().split('T')[0];
  }
  return dateStr;
};

const handleCoverSelect = (event) => {
  const file = event.target.files[0];
  if (file) {
    if (!file.type.startsWith('image/')) {
      errorMessage.value = 'Please select a valid image file (JPG, PNG, WebP).';
      selectedCoverImage.value = null;
      coverPreviewUrl.value = '';
      return;
    }
    selectedCoverImage.value = file;
    coverPreviewUrl.value = URL.createObjectURL(file);
    errorMessage.value = '';
  }
};

const removeCoverImage = () => {
  selectedCoverImage.value = null;
  coverPreviewUrl.value = '';
};

const handleLangFileSelect = (event, lang) => {
  const file = event.target.files[0];
  if (!file) return;
  if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
    errorMessage.value = 'Please select a valid PDF file.';
    return;
  }
  if (lang === 'en') selectedEnglishFile.value = file;
  else if (lang === 'si') selectedSinhalaFile.value = file;
  else if (lang === 'ta') selectedTamilFile.value = file;
  errorMessage.value = '';
};

const handleEditCoverSelect = (event) => {
  const file = event.target.files[0];
  if (file && file.type.startsWith('image/')) {
    editCoverFile.value = file;
    editRemoveCover.value = false;
  }
};

const handleEditLangFileSelect = (event, lang) => {
  const file = event.target.files[0];
  if (!file) return;
  if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
    editErrorMessage.value = 'Please select a valid PDF file.';
    return;
  }
  if (lang === 'en') {
    editEnglishFile.value = file;
    editRemoveEnglish.value = false;
  } else if (lang === 'si') {
    editSinhalaFile.value = file;
    editRemoveSinhala.value = false;
  } else if (lang === 'ta') {
    editTamilFile.value = file;
    editRemoveTamil.value = false;
  }
};

const fetchPublications = async () => {
  isLoadingList.value = true;
  try {
    const res = await instance.get('/api/v1/publications/all');
    publications.value = Array.isArray(res.data) ? res.data : [];
  } catch (err) {
    try {
      const publicRes = await instance.get('/api/v1/publications');
      publications.value = Array.isArray(publicRes.data) ? publicRes.data : [];
    } catch (e) {
      console.error('Failed to load publications:', e);
    }
  } finally {
    isLoadingList.value = false;
  }
};

const handleSubmit = async () => {
  if (!selectedEnglishFile.value && !selectedSinhalaFile.value && !selectedTamilFile.value) {
    errorMessage.value = 'Please upload at least one PDF attachment (English, Sinhala, or Tamil).';
    return;
  }

  isSubmitting.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    const formData = new FormData();
    formData.append('documentType', form.value.documentType);
    formData.append('title', form.value.title);
    formData.append('year', form.value.year);
    formData.append('description', form.value.description || '');
    formData.append('releaseDate', form.value.releaseDate);

    if (selectedCoverImage.value) {
      formData.append('coverImage', selectedCoverImage.value);
    }
    if (selectedEnglishFile.value) {
      formData.append('fileEnglish', selectedEnglishFile.value);
    }
    if (selectedSinhalaFile.value) {
      formData.append('fileSinhala', selectedSinhalaFile.value);
    }
    if (selectedTamilFile.value) {
      formData.append('fileTamil', selectedTamilFile.value);
    }

    await instance.post('/api/v1/publications', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    successMessage.value = `Publication "${form.value.title}" successfully uploaded and published!`;
    form.value.title = '';
    form.value.description = '';
    selectedCoverImage.value = null;
    coverPreviewUrl.value = '';
    selectedEnglishFile.value = null;
    selectedSinhalaFile.value = null;
    selectedTamilFile.value = null;

    await fetchPublications();
  } catch (err) {
    console.error('Upload failed:', err);
    errorMessage.value = err.response?.data?.message || err.response?.data || err.message || 'Failed to upload publication.';
  } finally {
    isSubmitting.value = false;
  }
};

const toggleActive = async (id) => {
  try {
    await instance.patch(`/api/v1/publications/${id}/toggle-status`);
    await fetchPublications();
  } catch (err) {
    console.error('Toggle status failed:', err);
  }
};

// Delete Handlers
const openDeleteModal = (pub) => {
  itemToDelete.value = pub;
  showDeleteModal.value = true;
};

const closeDeleteModal = () => {
  showDeleteModal.value = false;
  itemToDelete.value = null;
};

const executeDelete = async () => {
  if (!itemToDelete.value) return;
  isDeleting.value = true;
  try {
    await instance.delete(`/api/v1/publications/${itemToDelete.value.id}`);
    successMessage.value = `Publication "${itemToDelete.value.title}" was permanently removed.`;
    closeDeleteModal();
    await fetchPublications();
  } catch (err) {
    console.error('Delete failed:', err);
    errorMessage.value = 'Failed to delete publication. Please try again.';
  } finally {
    isDeleting.value = false;
  }
};

// Edit Handlers
const openEditModal = (pub) => {
  editForm.value = {
    id: pub.id,
    documentType: (pub.documentType || pub.category || 'ACTION_PLAN').toUpperCase(),
    title: pub.title || '',
    year: pub.year || '',
    releaseDate: formatReleaseDateForInput(pub.releaseDate),
    description: pub.description || '',
    currentCoverImageUrl: pub.coverImageUrl || '',
    currentEnglishUrl: pub.pdfEnglishUrl || pub.fileUrl || '',
    currentSinhalaUrl: pub.pdfSinhalaUrl || '',
    currentTamilUrl: pub.pdfTamilUrl || ''
  };

  editCoverFile.value = null;
  editRemoveCover.value = false;
  editEnglishFile.value = null;
  editRemoveEnglish.value = false;
  editSinhalaFile.value = null;
  editRemoveSinhala.value = false;
  editTamilFile.value = null;
  editRemoveTamil.value = false;
  editErrorMessage.value = '';
  showEditModal.value = true;
};

const closeEditModal = () => {
  showEditModal.value = false;
  editErrorMessage.value = '';
};

const handleUpdate = async () => {
  if (!editForm.value.title || !editForm.value.year || !editForm.value.releaseDate) {
    editErrorMessage.value = 'Please fill in all required fields.';
    return;
  }

  isUpdating.value = true;
  editErrorMessage.value = '';

  try {
    const formData = new FormData();
    formData.append('documentType', editForm.value.documentType);
    formData.append('title', editForm.value.title);
    formData.append('year', editForm.value.year);
    formData.append('description', editForm.value.description || '');
    formData.append('releaseDate', editForm.value.releaseDate);

    if (editCoverFile.value) {
      formData.append('coverImage', editCoverFile.value);
    }
    if (editRemoveCover.value) {
      formData.append('removeCoverImage', 'true');
    }

    if (editEnglishFile.value) {
      formData.append('fileEnglish', editEnglishFile.value);
    }
    if (editRemoveEnglish.value) {
      formData.append('removeEnglish', 'true');
    }

    if (editSinhalaFile.value) {
      formData.append('fileSinhala', editSinhalaFile.value);
    }
    if (editRemoveSinhala.value) {
      formData.append('removeSinhala', 'true');
    }

    if (editTamilFile.value) {
      formData.append('fileTamil', editTamilFile.value);
    }
    if (editRemoveTamil.value) {
      formData.append('removeTamil', 'true');
    }

    await instance.put(`/api/v1/publications/${editForm.value.id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    successMessage.value = `Publication "${editForm.value.title}" successfully updated!`;
    closeEditModal();
    await fetchPublications();
  } catch (err) {
    console.error('Update failed:', err);
    editErrorMessage.value = err.response?.data?.message || err.response?.data || err.message || 'Failed to update publication.';
  } finally {
    isUpdating.value = false;
  }
};

const getMediaUrl = (url) => {
  if (!url || url === '#') return '#';
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  const baseURL = instance.defaults?.baseURL || 'http://localhost:8096';
  return `${baseURL}${url.startsWith('/') ? '' : '/'}${url}`;
};

onMounted(fetchPublications);
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
