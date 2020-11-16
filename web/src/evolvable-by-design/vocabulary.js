const schema = 'http://schema.org/'
const evolvable = 'https://github.com/evolvable-by-design/use-cases/simba/vocab#'

export const vocabulary = {
  terms: {
    id: `${evolvable}id`,
    choiceId: `${evolvable}choiceId`,
    userId: `${evolvable}userId`,
    slug: `${evolvable}slug`,
    aslug: `${evolvable}adminSlug`,
    adminSlug: `${evolvable}adminSlug`,
    slugAdmin: `${evolvable}adminSlug`,
    token: `${evolvable}adminSlug`,
    title: `${evolvable}title`,
    description: `${schema}description`,
    createdAt: `${schema}dateCreated`,
    updatedAt: `${schema}dateModified`,
    mail: `${evolvable}email`,
    ics: `${evolvable}ics`,
    icsurl: `${evolvable}icsurl`,
    username: `${evolvable}username`,
    location: `${evolvable}pollLocation`,
    hasMeal: `${evolvable}has_meal`,
    endDate: `${schema}endDate`,
    startDate: `${schema}startDate`,
    users: `${evolvable}users`,
    auteur: `${schema}author`,
    content: `${evolvable}content`,
    clos: `${evolvable}closed`,
    padURL: `${evolvable}padURL`,
    pollChoices: `${evolvable}pollChoices`,
    pollComments: `${evolvable}pollComments`,
    selectedChoice: `${evolvable}selectedChoice`,
    tlkURL: `${evolvable}tlkURL`,
    choices: `${evolvable}pollChoices`,
  },
  types: {
    Id: `${evolvable}Id`,
    ChoiceId: `${evolvable}ChoiceId`,
    UserId: `${evolvable}UserId`,
    DateTime: `${schema}DateTime`,
    User: `${evolvable}User`,
    Users: `${evolvable}Users`,
    Choice: `${evolvable}Choice`,
    Choices: `${evolvable}Choices`,
    Comment: `${evolvable}Comment`,
    Comments: `${evolvable}Comments`,
    MealPreferences: `${evolvable}MealPreferences`,
    Poll: `${evolvable}Poll`,
  },
  actions: {
    createUser: `${evolvable}createUser`,
    createPoll: `${evolvable}createPoll`,
    getPoll: `${evolvable}getPoll`,
    getPollBySlug: `${evolvable}getPollBySlug`,
    getPollWithoutAdminSlug: `${evolvable}getPollWithoutAdminSlug`,
    updatePoll: `${evolvable}updatePoll`,
    addChoiceToPoll: `${evolvable}addChoiceToPoll`,
    deleteChoiceFromPoll: `${evolvable}deleteChoiceFromPoll`,
    updatePollChoice: `${evolvable}updatePollChoice`,
    commentPoll: `${evolvable}commentPoll`,
  },
  relations: {
    comment: `${evolvable}rel/comment`,
    nextComment: `${evolvable}rel/nextComment`,
    vote: `${evolvable}rel/vote`,
    nextVote: `${evolvable}rel/nextVote`,
    addChoice: `${evolvable}rel/addChoice`,
    delete: `${evolvable}rel/delete`,
    deleteChoice: `${evolvable}rel/deleteChoice`,
    update: `${evolvable}rel/update`,
    updateChoice: `${evolvable}rel/updateChoice`,
  }
}