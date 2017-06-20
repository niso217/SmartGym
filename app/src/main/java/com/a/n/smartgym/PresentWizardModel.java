/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.a.n.smartgym;

import android.content.Context;
import android.os.Bundle;

import com.a.n.smartgym.Adapter.ImageItem;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.repo.MuscleRepo;
import com.a.n.smartgym.wizard.model.AbstractWizardModel;
import com.a.n.smartgym.wizard.model.BranchPage;
import com.a.n.smartgym.wizard.model.InstructionPage;
import com.a.n.smartgym.wizard.model.ModelCallbacks;
import com.a.n.smartgym.wizard.model.MultipleFixedChoicePage;
import com.a.n.smartgym.wizard.model.MultipleSubChoicePage;
import com.a.n.smartgym.wizard.model.Page;
import com.a.n.smartgym.wizard.model.PageList;
import com.a.n.smartgym.wizard.model.ReviewItem;
import com.a.n.smartgym.wizard.model.SingleFixedChoicePage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class PresentWizardModel extends AbstractWizardModel{

    private List<ReviewItem> mCurrentReviewItems;


    public PresentWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                // BranchPage shows all of the branches available: Branch One, Branch Two, Branch Three. Each of these branches
                // have their own questions and the choices of the user will be summarised in the review section at the end
                new BranchPage(this, "Select one options")
                        .addBranch("Branch One",
                                new MultipleFixedChoicePage(this, "Question One")
                                        .setChoices(getData())

                                        .setRequired(true),

                                new MultipleSubChoicePage(this, "Question Two")

                        )

        );
    }

//    @Override
//    public void onPageDataChanged(Page changedPage) {
//        Bundle bundle = new Bundle();
//        bundle.putStringArray("key",new String[] {"1","2","3"});
//        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
//        for (Page page : this.getCurrentPageSequence()) {
//            page.getReviewItems(reviewItems);
//        }
//        mCurrentReviewItems = reviewItems;
////        Page mPage = this.findByKey(mCurrentReviewItems.get(1).getPageKey());
////        ((MultipleFixedChoicePage) mPage).setChoices(new String[] {"1","2","3"});
//
//    }

    private String[] getData() {

        List<String> arr = new ArrayList<>();
        Enumeration e = ExercisesDB.getInstance().keys.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            arr.add(key);

        }

        return arr.toArray(new String[arr.size()]);
    }


    private ArrayList<ImageItem> getData2() {


        MuscleRepo muscleRepo = new MuscleRepo();
        List<Muscle> exname = muscleRepo.getSubMuscle("","");

        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        for (int i = 0; i < exname.size(); i++) {
            imageItems.add(new ImageItem(exname.get(i).getImage(), exname.get(i).getName()));
        }

        return imageItems;

    }



}
